import * as React from 'react';
import { ShareAltOutlined } from '@ant-design/icons';
import {SsisControlTask, EntityType, OwnershipType, SearchResult} from '../../../types.generated';
import { Preview } from './preview/Preview';
import { Entity, EntityCapabilityType, IconStyleType, PreviewType } from '../Entity';
import { EntityProfile } from '../shared/containers/profile/EntityProfile';
import { useGetSsisControlTaskQuery } from '../../../graphql/ssisControlTask.generated';
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

/**
 * Definition of the DataHub SsisControlTask entity.
 */
export class SsisControlTaskEntity implements Entity<SsisControlTask> {
    type: EntityType = EntityType.SsisControlTask;

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

    isLineageEnabled = () => true;

    getAutoCompleteFieldName = () => 'name';

    getPathName = () => 'ssiscontroltasks';

    getEntityName = () => 'Ssis Control Task';

    getCollectionName = () => 'Ssis Control Tasks';

    renderProfile = (urn: string) => (
        <EntityProfile
            urn={urn}
            entityType={EntityType.SsisControlTask}
            useEntityQuery={useGetSsisControlTaskQuery}

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

    getOverridePropertiesFromEntity = (ssisControlTask?: SsisControlTask | null): GenericEntityProperties => {
        // TODO: Get rid of this once we have correctly formed platform coming back.
        const name = ssisControlTask?.properties?.name;
        const externalUrl = ssisControlTask?.properties?.externalUrl;
        return {
            name,
            externalUrl,
        };
    };

    renderPreview = (_: PreviewType, data: SsisControlTask) => {
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
        const data = result.entity as SsisControlTask;
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

                deprecation={data.deprecation}
                degree={(result as any).degree}
                paths={(result as any).paths}
            />
        );
    };

    displayName = (data: SsisControlTask) => {
        return data.properties?.name || data.urn;
    };

    getGenericEntityProperties = (data: SsisControlTask) => {
        return getDataForEntityType({
            data,
            entityType: this.type,
            getOverrideProperties: this.getOverridePropertiesFromEntity,
        });
    };

    getLineageVizConfig = (entity: SsisControlTask) => {
        return {
            urn: entity?.urn,
            name: entity?.properties?.name || entity.name,
            expandedName:  entity?.properties?.name || entity.name,
            type: EntityType.SsisControlTask,
            subtype: undefined,
            icon: entity?.flow?.ssisPackage?.platform?.properties?.logoUrl || undefined,
            platform: entity?.flow?.ssisPackage?.platform || undefined

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
