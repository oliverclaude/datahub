import * as React from 'react';
import { ShareAltOutlined } from '@ant-design/icons';
import {SsisPackage, EntityType, OwnershipType, SearchResult} from '../../../types.generated';
import { Preview } from './preview/Preview';
import { Entity, EntityCapabilityType, IconStyleType, PreviewType } from '../Entity';
import { EntityProfile } from '../shared/containers/profile/EntityProfile';
import { useGetSsisPackageQuery } from '../../../graphql/ssisPackage.generated';
import { DocumentationTab } from '../shared/tabs/Documentation/DocumentationTab';
import { PropertiesTab } from '../shared/tabs/Properties/PropertiesTab';
import { SidebarAboutSection } from '../shared/containers/profile/sidebar/AboutSection/SidebarAboutSection';
import { SidebarTagsSection } from '../shared/containers/profile/sidebar/SidebarTagsSection';
import { SidebarOwnerSection } from '../shared/containers/profile/sidebar/Ownership/sidebar/SidebarOwnerSection';
import { GenericEntityProperties } from '../shared/types';
import { getDataForEntityType } from '../shared/containers/profile/utils';
import { SidebarDomainSection } from '../shared/containers/profile/sidebar/Domain/SidebarDomainSection';
import { EntityMenuItems } from '../shared/EntityDropdown/EntityDropdown';
import { capitalizeFirstLetterOnly } from '../../shared/textUtil';
import DataProductSection from '../shared/containers/profile/sidebar/DataProduct/DataProductSection';
import { SsisPackageControlFlowTab ,} from './tabs/SsisPackageControlFlowTab';
import {SsisPackageDataFlowTab} from "./tabs/SsisPackageDataFlowTab";
import {useEntityRegistry} from "../../useEntityRegistry";

/**
 * Definition of the DataHub SsisPackage entity.
 */
export class SsisPackageEntity implements Entity<SsisPackage> {
    type: EntityType = EntityType.SsisPackage;

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

    getPathName = () => 'ssispackages';

    getEntityName = () => 'Ssis Package';

    getCollectionName = () => 'Ssis Packages';

    renderProfile = (urn: string) => (
        <EntityProfile
            urn={urn}
            entityType={EntityType.SsisPackage}
            useEntityQuery={useGetSsisPackageQuery}
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
                    name: 'Control Flow',
                    component: SsisPackageControlFlowTab,
                },
                {
                    name: 'Data Flow',
                    component: SsisPackageDataFlowTab,
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
                // {
                //     component: ControlFlowSection,
                // },
                // {
                //     component: DataFlowSection,
                // }
            ]}
        />
    );

    getOverridePropertiesFromEntity = (ssisPackage?: SsisPackage | null): GenericEntityProperties => {
        // TODO: Get rid of this once we have correctly formed platform coming back.
        const name = ssisPackage?.properties?.name;
        const externalUrl = ssisPackage?.properties?.externalUrl;
        return {
            name,
            externalUrl,
        };
    };

    renderPreview = (_: PreviewType, data: SsisPackage) => {
        const entityRegistry = useEntityRegistry();
        const genericProperties = entityRegistry.getGenericEntityProperties(EntityType.SsisPackage, data);

        return (
            <Preview
                urn={data.urn}
                name={data.name || ''}
                description={genericProperties?.editableProperties?.description || data.properties?.description}
                platformName={
                    genericProperties?.platform?.properties?.displayName || capitalizeFirstLetterOnly(genericProperties?.platform?.name)
                }
                platformLogo={genericProperties?.platform?.properties?.logoUrl || ''}
                owners={genericProperties?.ownership?.owners}
                globalTags={genericProperties?.globalTags}
                domain={genericProperties?.domain?.domain}
                externalUrl={genericProperties?.properties?.sourceUrl}
            />
        );
    };

    renderSearch = (result: SearchResult) => {

        const data = result.entity as SsisPackage;
        const entityRegistry = useEntityRegistry();
        const genericProperties = entityRegistry.getGenericEntityProperties(EntityType.SsisPackage, data);

        return (
            <Preview
                urn={data.urn}
                name={genericProperties?.properties?.name || ''}
                platformInstanceId={genericProperties?.dataPlatformInstance?.instanceId}
                description={genericProperties?.editableProperties?.description || data.properties?.description || ''}
                platformName={
                    genericProperties?.platform?.properties?.displayName || capitalizeFirstLetterOnly(data?.platform?.name)
                }
                platformLogo={genericProperties?.platform?.properties?.logoUrl || ''}
                owners={genericProperties?.ownership?.owners}
                globalTags={genericProperties?.globalTags}
                insights={result.insights}
                domain={genericProperties?.domain?.domain}
                externalUrl={genericProperties?.properties?.sourceUrl}
                deprecation={genericProperties?.deprecation}
                degree={(result as any).degree}
                paths={(result as any).paths}
            />
        );
    };

    displayName = (data: SsisPackage) => {
        return data.properties?.name || data.urn;
    };

    getGenericEntityProperties = (data: SsisPackage) => {
        return getDataForEntityType({
            data,
            entityType: this.type,
            getOverrideProperties: this.getOverridePropertiesFromEntity,
        });
    };

    getLineageVizConfig = (entity: SsisPackage) => {
        return {
            urn: entity?.urn,
            name: entity?.properties?.name || entity.name,
            expandedName:  entity?.properties?.name || entity.name,
            type: EntityType.SsisPackage,
            subtype: undefined,
            icon: entity?.platform?.properties?.logoUrl || undefined,
            platform: entity?.platform || undefined

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
