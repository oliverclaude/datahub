import React from 'react';
import {EntityType, SsisPackage} from '../../../../types.generated';
import {useBaseEntity} from "../../shared/EntityContext";
import {useEntityRegistry} from "../../../useEntityRegistry";
import {EntityList} from "../../shared/tabs/Entity/components/EntityList";

export const SsisPackageDataFlowTab = () => {
    const entity = useBaseEntity<SsisPackage>() as any;
    const ssisPackage = entity && entity.ssisPackage;
    const dataFlows = ssisPackage?.childDataFlow?.relationships.map((relationship) => relationship.entity);

    const entityRegistry = useEntityRegistry();
    const totalTasks = dataFlows?.childTasks?.total || 0;
    const title = `Contains ${totalTasks} ${
        totalTasks === 1
            ? entityRegistry.getEntityName(EntityType.SsisDataflow)
            : entityRegistry.getCollectionName(EntityType.SsisDataflow)
    }`;
    return <EntityList title={title} type={EntityType.SsisDataflow} entities={dataFlows || []} />;
};
