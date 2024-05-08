import React from 'react';
import {EntityType, SsisControlFlow} from '../../../../types.generated';
import { useEntityRegistry } from '../../../useEntityRegistry';
import {useBaseEntity} from "../../shared/EntityContext";
import {EntityList} from "../../shared/tabs/Entity/components/EntityList";

export const SsisControlFlowTaskTab = () => {
    const entity = useBaseEntity<SsisControlFlow>() as any;
    const ssisControlFlow = entity && entity.ssisControlFlow;
    const controlTasks = ssisControlFlow?.childTasks?.relationships.map((relationship) => relationship.entity);
    const entityRegistry = useEntityRegistry();
    const totalTasks = ssisControlFlow?.childTasks?.total || 0;
    const title = `Contains ${totalTasks} ${
        totalTasks === 1
            ? entityRegistry.getEntityName(EntityType.SsisControlTask)
            : entityRegistry.getCollectionName(EntityType.SsisControlTask)
    }`;
    return <EntityList title={title} type={EntityType.SsisControlTask} entities={controlTasks || []} />;
};
