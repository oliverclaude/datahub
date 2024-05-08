import re
from dataclasses import dataclass, field
from typing import Callable, Iterable, List, Optional

from datahub.api.entities.ssis.ssisbase import SsisBaseEntity
from datahub.emitter.generic_emitter import Emitter
from datahub.emitter.mcp import MetadataChangeProposalWrapper
from datahub.metadata.schema_classes import (
    EditableSsisDataFlowPropertiesClass,
    SsisDataFlowInfoClass,
    SsisDataFlowInputOutputClass,
    StatusClass,
)
from datahub.utilities.urns.dataset_urn import DatasetUrn


@dataclass
class SsisDataFlow(SsisBaseEntity):
    """This is a SsisDataFlow class which represent a SsisDataFlow.

    Args:
        id (str): The id of the dataflow instance execution. This should be unique per execution but not needed to be globally unique.
        package_urn: str
        controltask_urn: str
        properties Dict[str, str]: Custom properties to set for the DataProcessInstance
        url (Optional[str]): Url which points to the SsisPackage at the orchestrator
        owners Set[str]): A list of user ids that own this job.
        group_owners Set[str]): A list of group ids that own this job.
        inlets (List[str]): List of urns the DataProcessInstance consumes
        outlets (List[str]): List of urns the DataProcessInstance produces

    """

    package_urn: str = "unspecified"
    controltask_urn: str = "unspecified"
    inlets: List[DatasetUrn] = field(default_factory=list)
    outlets: List[DatasetUrn] = field(default_factory=list)

    def __post_init__(self):
        self.urn = self.create_urn(self.package_urn, self.id)

    def create_urn(self, flow_urn: str, id: str) -> str:
        return f"urn:li:ssisDataFlow:({flow_urn},{id})"

    def get_orchestrator_name(self) -> str:
        regex = re.compile(r"urn:li:ssisPackage:\((.*),(.*),(.*)\)")
        match = regex.search(self.package_urn)
        return match.group(3)

    def generate_mcp(
        self, materialize_iolets: bool = True
    ) -> Iterable[MetadataChangeProposalWrapper]:
        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=SsisDataFlowInfoClass(
                name=self.name if self.name is not None else self.id,
                description=self.description,
                customProperties=self.properties,
                externalUrl=self.url,
                controlTaskUrn=self.controltask_urn,
            ),
        )
        yield mcp

        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=EditableSsisDataFlowPropertiesClass(description=self.description),
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
        Emit the SsisSsisPackage entity to Datahub

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
            aspect=SsisDataFlowInputOutputClass(
                inputDatasets=[str(urn) for urn in self.inlets],
                outputDatasets=[str(urn) for urn in self.outlets],
                inputControlTask=self.controltask_urn,
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
