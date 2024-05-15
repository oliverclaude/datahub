from dataclasses import dataclass
from typing import Callable, Iterable, Optional

from datahub.api.entities.ssis.ssisbase import SsisBaseEntity
from datahub.emitter.generic_emitter import Emitter
from datahub.emitter.mcp import MetadataChangeProposalWrapper
from datahub.metadata.schema_classes import (
    EditableSsisPackagePropertiesClass,
    SsisPackageInfoClass,
)


@dataclass
class SsisPackage(SsisBaseEntity):
    """This is a SsisPackage class which represent a SsisPackage.

    Args:
        id (str): The id of the dataflow instance execution. This should be unique per execution but not needed to be globally unique.
        orchestrator: str
        cluster: str

        properties Dict[str, str]: Custom properties to set for the DataProcessInstance
        url (Optional[str]): Url which points to the SsisPackage at the orchestrator
        owners Set[str]): A list of user ids that own this job.
        group_owners Set[str]): A list of group ids that own this job.

    """

    orchestrator: str = "unspecified"
    cluster: str = "unspecified"

    def __post_init__(self):
        self.urn = self.create_urn(self.orchestrator, self.cluster, self.id)

    def get_orchestrator_name(self) -> str:
        return self.orchestrator

    def create_urn(self, orchestrator: str, cluster: str, id: str) -> str:
        return f"urn:li:ssisPackage:({orchestrator},{id},{cluster})"

    def generate_mcp(
        self, materialize_iolets: bool = True
    ) -> Iterable[MetadataChangeProposalWrapper]:
        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=SsisPackageInfoClass(
                name=self.name if self.name is not None else self.id,
                description=self.description,
                customProperties=self.properties,
                externalUrl=self.url,
            ),
        )
        yield mcp

        mcp = MetadataChangeProposalWrapper(
            entityUrn=str(self.urn),
            aspect=EditableSsisPackagePropertiesClass(description=self.description),
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
        Emit the SsisPackage entity to Datahub

        :param emitter: Datahub Emitter to emit the process event
        :param callback: (Optional[Callable[[Exception, str], None]]) the callback method for KafkaEmitter if it is used
        """

        for mcp in self.generate_mcp():
            emitter.emit(mcp, callback)
