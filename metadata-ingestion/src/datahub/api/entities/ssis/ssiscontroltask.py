import re
from dataclasses import dataclass, field
from typing import Callable, Iterable, List, Optional

from datahub.api.entities.ssis.ssisbase import SsisBaseEntity
from datahub.emitter.generic_emitter import Emitter
from datahub.emitter.mcp import MetadataChangeProposalWrapper
from datahub.metadata._schema_classes import EditableSsisControlTaskPropertiesClass
from datahub.metadata.schema_classes import (
    SsisControlTaskInfoClass,
    SsisControlTaskInputOutputClass,
    StatusClass,
)
from datahub.utilities.urns.dataset_urn import DatasetUrn


@dataclass
class SsisControlTask(SsisBaseEntity):
    """This is a SsisControlTask class which represent a SsisControlTask.

    Args:
        id (str): The id of the dataflow instance execution. This should be unique per execution but not needed to be globally unique.
        controlflow_urn: str
        input_control_tasks: List[str] = field(default_factory=list)
        properties Dict[str, str]: Custom properties to set for the DataProcessInstance
        url (Optional[str]): Url which points to the SsisPackage at the orchestrator
        owners Set[str]): A list of user ids that own this job.
        group_owners Set[str]): A list of group ids that own this job.
        inlets (List[str]): List of urns the DataProcessInstance consumes
        outlets (List[str]): List of urns the DataProcessInstance produces

    """

    flow_urn: str = "unspecified"
    type: str = "SCRIPT"
    input_control_tasks: List[str] = field(default_factory=list)
    inlets: List[DatasetUrn] = field(default_factory=list)
    outlets: List[DatasetUrn] = field(default_factory=list)

    def __post_init__(self):
        self.urn = self.create_urn(self.flow_urn, self.id)

    def get_orchestrator_name(self) -> str:
        regex = re.compile(
            r"urn:li:ssisControlFlow:\(urn:li:ssisPackage:\((.*),(.*),(.*)\),.*"
        )
        match = regex.search(self.flow_urn)
        return match.group(3)

    def get_package_urn(self) -> str:
        regex = re.compile(r"urn:li:ssisControlFlow:\((urn:li:ssisPackage:.*),.*")
        match = regex.search(self.flow_urn)
        return match.group(1)

    def create_urn(self, flow_urn: str, id: str) -> str:
        return f"urn:li:ssisControlTask:({flow_urn},{id})"

    def generate_mcp(
        self, materialize_iolets: bool = True
    ) -> Iterable[MetadataChangeProposalWrapper]:
        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=SsisControlTaskInfoClass(
                name=self.name if self.name is not None else self.id,
                description=self.description,
                customProperties=self.properties,
                externalUrl=self.url,
                controlFlowUrn=self.flow_urn,
                type=self.type,
            ),
        )
        yield mcp

        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=EditableSsisControlTaskPropertiesClass(description=self.description),
        )
        yield mcp

        yield from self.generate_data_input_output_mcp(
            materialize_iolets=materialize_iolets
        )

        for owner in self.generate_ownership_aspect():
            mcp = MetadataChangeProposalWrapper(
                entityUrn=str(self.urn),
                aspect=owner,
            )
            yield mcp

        for tag in self.generate_tags_aspect():
            mcp = MetadataChangeProposalWrapper(
                entityUrn=str(self.urn),
                aspect=tag,
            )
            yield mcp

    def emit(
        self,
        emitter: Emitter,
        callback: Optional[Callable[[Exception, str], None]] = None,
    ) -> None:
        """
        Emit the SsisControlTask entity to Datahub

        :param emitter: Datahub Emitter to emit the process event
        :param callback: (Optional[Callable[[Exception, str], None]]) the callback method for KafkaEmitter if it is used
        """

        for mcp in self.generate_mcp():
            emitter.emit(mcp, callback)

    def generate_data_input_output_mcp(
        self, materialize_iolets: bool
    ) -> Iterable[MetadataChangeProposalWrapper]:
        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=SsisControlTaskInputOutputClass(
                inputDatasets=[str(urn) for urn in self.inlets],
                outputDatasets=[str(urn) for urn in self.outlets],
                inputControlTasks=self.input_control_tasks,
            ),
        )
        yield mcp

        # Force entity materialization
        if materialize_iolets:
            for iolet in self.inlets + self.outlets:
                yield MetadataChangeProposalWrapper(
                    entityUrn=str(iolet),
                    aspect=StatusClass(removed=False),
                )
