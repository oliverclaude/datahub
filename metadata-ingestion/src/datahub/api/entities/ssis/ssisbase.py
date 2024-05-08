from dataclasses import dataclass, field
from typing import Dict, Iterable, Optional, Set

import datahub.emitter.mce_builder as builder
from datahub.metadata._schema_classes import (
    AuditStampClass,
    GlobalTagsClass,
    OwnerClass,
    OwnershipClass,
    OwnershipSourceClass,
    OwnershipSourceTypeClass,
    OwnershipTypeClass,
    TagAssociationClass,
)


@dataclass
class SsisBaseEntity:

    id: str
    urn: str = field(init=False)
    name: Optional[str] = None
    description: Optional[str] = None
    url: Optional[str] = None
    properties: Dict[str, str] = field(default_factory=dict)
    tags: Set[str] = field(default_factory=set)
    owners: Set[str] = field(default_factory=set)
    group_owners: Set[str] = field(default_factory=set)

    def generate_ownership_aspect(self) -> Iterable[OwnershipClass]:
        owners = set([builder.make_user_urn(owner) for owner in self.owners]) | set(
            [builder.make_group_urn(owner) for owner in self.group_owners]
        )
        ownership = OwnershipClass(
            owners=[
                OwnerClass(
                    owner=urn,
                    type=OwnershipTypeClass.DEVELOPER,
                    source=OwnershipSourceClass(
                        type=OwnershipSourceTypeClass.SERVICE,
                        # url=dag.filepath,
                    ),
                )
                for urn in (owners or [])
            ],
            lastModified=AuditStampClass(
                time=0,
                actor=builder.make_user_urn(self.get_orchestrator_name()),
            ),
        )
        return [ownership]

    def get_orchestrator_name(self):
        pass

    def generate_tags_aspect(self) -> Iterable[GlobalTagsClass]:
        tags = GlobalTagsClass(
            tags=[
                TagAssociationClass(tag=builder.make_tag_urn(tag))
                for tag in (sorted(self.tags) or [])
            ]
        )
        return [tags]
