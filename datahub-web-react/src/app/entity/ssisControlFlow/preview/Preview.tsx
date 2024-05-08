import React from 'react';
import { Typography } from 'antd';
import styled from 'styled-components';
import {
    Deprecation,
    Domain,
    EntityPath,
    EntityType,
    GlobalTags,
    Owner,
    SearchInsight,
} from '../../../../types.generated';
import DefaultPreviewCard from '../../../preview/DefaultPreviewCard';
import { useEntityRegistry } from '../../../useEntityRegistry';
import { IconStyleType } from '../../Entity';
import { ANTD_GRAY } from '../../shared/constants';

const StatText = styled(Typography.Text)`
    color: ${ANTD_GRAY[8]};
`;

export const Preview = ({
    urn,
    name,
    platformInstanceId,
    description,
    platformName,
    platformLogo,
    owners,
    globalTags,
    domain,
    externalUrl,
    snippet,
    insights,
    deprecation,
    degree,
    paths,
    taskCount,
}: {
    urn: string;
    name: string;
    platformInstanceId?: string;
    description?: string | null;
    platformName?: string;
    platformLogo?: string | null;
    owners?: Array<Owner> | null;
    domain?: Domain | null;
    globalTags?: GlobalTags | null;
    deprecation?: Deprecation | null;
    externalUrl?: string | null;
    snippet?: React.ReactNode | null;
    insights?: Array<SearchInsight> | null;
    degree?: number;
    paths?: EntityPath[];
    taskCount?: number;
}): JSX.Element => {
    const entityRegistry = useEntityRegistry();
    return (
        <DefaultPreviewCard
            url={entityRegistry.getEntityUrl(EntityType.SsisControlFlow, urn)}
            name={name}
            urn={urn}
            description={description || ''}
            platformInstanceId={platformInstanceId}
            type="Ssis Control Flow"
            typeIcon={entityRegistry.getIcon(EntityType.SsisControlFlow, 14, IconStyleType.ACCENT)}
            platform={platformName}
            logoUrl={platformLogo || ''}
            owners={owners}
            tags={globalTags || undefined}
            domain={domain}
            snippet={snippet}
            insights={insights}
            externalUrl={externalUrl}
            deprecation={deprecation}
            degree={degree}
            paths={paths}
            subHeader={
                (taskCount && [
                    <StatText>
                        <b>{taskCount}</b> {entityRegistry.getCollectionName(EntityType.SsisControlFlow)}
                    </StatText>,
                ]) ||
                undefined
            }

        />
    );
};
