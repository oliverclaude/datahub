import * as React from 'react';
import { ShareAltOutlined } from '@ant-design/icons';
import {SsisControlFlow, EntityType, OwnershipType, SearchResult} from '../../../types.generated';
import { Preview } from './preview/Preview';
import { Entity, EntityCapabilityType, IconStyleType, PreviewType } from '../Entity';
import { EntityProfile } from '../shared/containers/profile/EntityProfile';
import { useGetSsisControlFlowQuery } from '../../../graphql/ssisControlFlow.generated';
import { DocumentationTab } from '../shared/tabs/Documentation/DocumentationTab';
import { PropertiesTab } from '../shared/tabs/Properties/PropertiesTab';
import { SidebarAboutSection } from '../shared/containers/profile/sidebar/AboutSection/SidebarAboutSection';
import { SidebarTagsSection } from '../shared/containers/profile/sidebar/SidebarTagsSection';
import { SidebarOwnerSection } from '../shared/containers/profile/sidebar/Ownership/sidebar/SidebarOwnerSection';
import { GenericEntityProperties } from '../shared/types';

import { getDataForEntityType } from '../shared/containers/profile/utils';
import { SidebarDomainSection } from '../shared/containers/profile/sidebar/Domain/SidebarDomainSection';
import { EntityMenuItems } from '../shared/EntityDropdown/EntityDropdown';
import DataProductSection from '../shared/containers/profile/sidebar/DataProduct/DataProductSection';
import {SsisControlFlowTaskTab} from "./tabs/SsisControlFlowTaskTab";

/**
 * Definition of the DataHub SsisControlFlow entity.
 */
export class SsisControlFlowEntity implements Entity<SsisControlFlow> {
    type: EntityType = EntityType.SsisControlFlow;

    icon = (fontSize: number, styleType: IconStyleType, color?: string) => {
        if (styleType === IconStyleType.TAB_VIEW) {
            return <ShareAltOutlined style={{ fontSize, color }} />;
        }

        if (styleType === IconStyleType.HIGHLIGHT) {
            return <ShareAltOutlined style={{ fontSize, color: color || '#d6246c' }} />;
        }

        return (
            <ShareAltOutlined
                style={{
                    fontSize,
                    color: color || '#BFBFBF',
                }}
            />
        );
    };

    isSearchEnabled = () => true;

    isBrowseEnabled = () => true;

    isLineageEnabled = () => false;

    getAutoCompleteFieldName = () => 'name';

    getPathName = () => 'ssiscontrolflow';

    getEntityName = () => 'Ssis Control Flow';

    getCollectionName = () => 'Ssis Control Flows';

    renderProfile = (urn: string) => (
        <EntityProfile
            urn={urn}
            entityType={EntityType.SsisControlFlow}
            useEntityQuery={useGetSsisControlFlowQuery}

            getOverrideProperties={this.getOverridePropertiesFromEntity}
            headerDropdownItems={new Set([EntityMenuItems.UPDATE_DEPRECATION])}
            tabs={[
                {
                    name: 'Documentation',
                    component: DocumentationTab,
                },
                {
                    name: 'Properties',
                    component: PropertiesTab,
                },
                {
                    name: 'Tasks',
                    component: SsisControlFlowTaskTab,
                },
            ]}
            sidebarSections={[
                {
                    component: SidebarAboutSection,
                },
                {
                    component: SidebarOwnerSection,
                    properties: {
                        defaultOwnerType: OwnershipType.TechnicalOwner,
                    },
                },
                {
                    component: SidebarTagsSection,
                    properties: {
                        hasTags: true,
                        hasTerms: true,
                    },
                },
                {
                    component: SidebarDomainSection,
                },
                {
                    component: DataProductSection,
                },
            ]}
        />
    );

    getOverridePropertiesFromEntity = (ssisControlFlow?: SsisControlFlow | null): GenericEntityProperties => {
        // TODO: Get rid of this once we have correctly formed platform coming back.
        const name = ssisControlFlow?.properties?.name;
        const externalUrl = ssisControlFlow?.properties?.externalUrl;
        return {
            name,
            externalUrl,
        };
    };

    renderPreview = (_: PreviewType, data: SsisControlFlow) => {
        return (
            <Preview
                urn={data.urn}
                name={data.properties?.name || ''}
                description={data.editableProperties?.description || data.properties?.description}

                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                domain={data.domain?.domain}

                externalUrl={data.properties?.externalUrl}
            />
        );
    };

    renderSearch = (result: SearchResult) => {
        const data = result.entity as SsisControlFlow;
        return (
            <Preview
                urn={data.urn}
                name={data.properties?.name || ''}
                platformInstanceId={data.dataPlatformInstance?.instanceId}
                description={data.editableProperties?.description || data.properties?.description || ''}

                owners={data.ownership?.owners}
                globalTags={data.globalTags}
                insights={result.insights}
                domain={data.domain?.domain}

                externalUrl={data.properties?.externalUrl}
                taskCount={(data as any).childTasks?.total}
                deprecation={data.deprecation}
                degree={(result as any).degree}
                paths={(result as any).paths}
            />
        );
    };

    displayName = (data: SsisControlFlow) => {
        return data.properties?.name || data.urn;
    };

    getGenericEntityProperties = (data: SsisControlFlow) => {
        return getDataForEntityType({
            data,
            entityType: this.type,
            getOverrideProperties: this.getOverridePropertiesFromEntity,
        });
    };

    getLineageVizConfig = (entity: SsisControlFlow) => {
        return {
            urn: entity?.urn,
            name: entity?.properties?.name || entity.name,
            expandedName:  entity?.properties?.name || entity.name,
            type: EntityType.SsisControlFlow,
            subtype: undefined,
            icon: entity?.ssisPackage?.platform?.properties?.logoUrl || undefined,
            platform: entity?.ssisPackage?.platform || undefined

        };
    };

    supportedCapabilities = () => {
        return new Set([
            EntityCapabilityType.OWNERS,
            EntityCapabilityType.GLOSSARY_TERMS,
            EntityCapabilityType.TAGS,
            EntityCapabilityType.DOMAINS,
            EntityCapabilityType.DEPRECATION,
            EntityCapabilityType.SOFT_DELETE,
        ]);
    };
}
