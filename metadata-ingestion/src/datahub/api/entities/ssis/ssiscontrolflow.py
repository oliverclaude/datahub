import re
from dataclasses import dataclass
from typing import Callable, Iterable, Optional

from datahub.api.entities.ssis.ssisbase import SsisBaseEntity
from datahub.emitter.generic_emitter import Emitter
from datahub.emitter.mcp import MetadataChangeProposalWrapper
from datahub.metadata._schema_classes import EditableSsisControlFlowPropertiesClass
from datahub.metadata.schema_classes import SsisControlFlowInfoClass


@dataclass
class SsisControlFlow(SsisBaseEntity):
    """This is a SsisControlFlow class which represent a SsisControlFlow.

    Args:
        id (str): The id of the dataflow instance execution. This should be unique per execution but not needed to be globally unique.
        package_urn: str
        properties Dict[str, str]: Custom properties to set for the DataProcessInstance
        url (Optional[str]): Url which points to the SsisPackage at the orchestrator
        owners Set[str]): A list of user ids that own this job.
        group_owners Set[str]): A list of group ids that own this job.

    """

    package_urn: str = None

    def __post_init__(self):
        self.urn = self.create_urn(self.package_urn, self.id)

    def create_urn(self, package_urn: str, id: str) -> str:
        return f"urn:li:ssisControlFlow:({package_urn},{id})"

    def get_orchestrator_name(self) -> str:
        regex = re.compile(r"urn:li:ssisPackage:\((.*),(.*),(.*)\)")
        match = regex.search(self.package_urn)
        return match.group(3)

    def generate_mcp(
        self, materialize_iolets: bool = True
    ) -> Iterable[MetadataChangeProposalWrapper]:
        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=SsisControlFlowInfoClass(
                name=self.name if self.name is not None else self.id,
                description=self.description,
                customProperties=self.properties,
                externalUrl=self.url,
                packageUrn=self.package_urn,
            ),
        )
        yield mcp

        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=EditableSsisControlFlowPropertiesClass(description=self.description),
        )
        yield mcp

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
        Emit the SsisControlFlow entity to Datahub

        :param emitter: Datahub Emitter to emit the process event
        :param callback: (Optional[Callable[[Exception, str], None]]) the callback method for KafkaEmitter if it is used
        """

        for mcp in self.generate_mcp():
            emitter.emit(mcp, callback)
