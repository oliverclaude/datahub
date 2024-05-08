import React from 'react';
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
}): JSX.Element => {
    const entityRegistry = useEntityRegistry();
    return (
        <DefaultPreviewCard
            url={entityRegistry.getEntityUrl(EntityType.SsisPackage, urn)}
            name={name}
            urn={urn}
            description={description || ''}
            platformInstanceId={platformInstanceId}
            type="Ssis Package"
            typeIcon={entityRegistry.getIcon(EntityType.SsisPackage, 14, IconStyleType.ACCENT)}
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
        />
    );
};
