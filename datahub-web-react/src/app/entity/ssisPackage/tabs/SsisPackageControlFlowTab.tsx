import React from 'react';
import {EntityType, SsisPackage} from '../../../../types.generated';
import {useBaseEntity} from "../../shared/EntityContext";
import {useEntityRegistry} from "../../../useEntityRegistry";
import {EntityList} from "../../shared/tabs/Entity/components/EntityList";

export const SsisPackageControlFlowTab = () => {
    const entity = useBaseEntity<SsisPackage>() as any;
    const ssisPackage = entity && entity.ssisPackage;
    const controlFlows = ssisPackage?.childControlFlow?.relationships.map((relationship) => relationship.entity);
    const entityRegistry = useEntityRegistry();
    const totalTasks = controlFlows?.childTasks?.total || 0;
    const title = `Contains ${totalTasks} ${
        totalTasks === 1
            ? entityRegistry.getEntityName(EntityType.SsisControlFlow)
            : entityRegistry.getCollectionName(EntityType.SsisControlFlow)
    }`;
    return <EntityList title={title} type={EntityType.SsisControlFlow} entities={controlFlows || []} />;
};

