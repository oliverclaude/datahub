package com.linkedin.datahub.graphql.types.ssis.mappers;

import static com.linkedin.datahub.graphql.types.ssis.SsisPackageType.*;
import static com.linkedin.metadata.Constants.*;

import com.linkedin.common.DataPlatformInstance;
import com.linkedin.common.Deprecation;
import com.linkedin.common.GlobalTags;
import com.linkedin.common.GlossaryTerms;
import com.linkedin.common.InstitutionalMemory;
import com.linkedin.common.Ownership;
import com.linkedin.common.Status;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.DataMap;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.*;
import com.linkedin.datahub.graphql.types.common.mappers.*;
import com.linkedin.datahub.graphql.types.common.mappers.util.MappingHelper;
import com.linkedin.datahub.graphql.types.common.mappers.util.SystemMetadataUtils;
import com.linkedin.datahub.graphql.types.domain.DomainAssociationMapper;
import com.linkedin.datahub.graphql.types.glossary.mappers.GlossaryTermsMapper;
import com.linkedin.datahub.graphql.types.mappers.ModelMapper;
import com.linkedin.datahub.graphql.types.structuredproperty.StructuredPropertiesMapper;
import com.linkedin.datahub.graphql.types.tag.mappers.GlobalTagsMapper;
import com.linkedin.domain.Domains;
import com.linkedin.entity.EntityResponse;
import com.linkedin.entity.EnvelopedAspectMap;
import com.linkedin.metadata.key.DataPlatformKey;
import com.linkedin.metadata.utils.EntityKeyUtils;
import com.linkedin.structured.StructuredProperties;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SsisPackageMapper implements ModelMapper<EntityResponse, SsisPackage> {

  public static final SsisPackageMapper INSTANCE = new SsisPackageMapper();

  public static SsisPackage map(
      @Nullable final QueryContext context, @Nonnull final EntityResponse ssisPackage) {
    return INSTANCE.apply(context, ssisPackage);
  }

  public SsisPackage apply(
      @Nullable final QueryContext context, @Nonnull final EntityResponse entityResponse) {
    SsisPackage result = new SsisPackage();
    Urn entityUrn = entityResponse.getUrn();
    result.setUrn(entityResponse.getUrn().toString());
    result.setType(EntityType.SSIS_PACKAGE);

    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    Long lastIngested = SystemMetadataUtils.getLastIngestedTime(aspectMap);
    result.setLastIngested(lastIngested);

    //    SSISPACKAGE_INFO_ASPECT_NAME,
    //    SSISPACKAGE_KEY_ASPECT_NAME,
    //    EDITABLE_SSISPACKAGE_PROPERTIES_ASPECT_NAME,
    //    INSTITUTIONAL_MEMORY_ASPECT_NAME,
    //    OWNERSHIP_ASPECT_NAME,
    //    STATUS_ASPECT_NAME,
    //    GLOBAL_TAGS_ASPECT_NAME,
    //    GLOSSARY_TERMS_ASPECT_NAME,
    //    DOMAINS_ASPECT_NAME,
    //    DEPRECATION_ASPECT_NAME,
    //    DATA_PLATFORM_INSTANCE_ASPECT_NAME,
    //    BROWSE_PATHS_V2_ASPECT_NAME,
    //    STRUCTURED_PROPERTIES_ASPECT_NAME);

    //    BROWSE_PATHS_ASPECT_NAME,

    MappingHelper<SsisPackage> mappingHelper = new MappingHelper<>(aspectMap, result);

    mappingHelper.mapToResult(SSISPACKAGE_KEY_ASPECT_NAME, this::mapSsisPackageKey);

    mappingHelper.mapToResult(SSISPACKAGE_INFO_ASPECT_NAME, this::mapSsisPackageInfo);

    mappingHelper.mapToResult(
        EDITABLE_SSISPACKAGE_PROPERTIES_ASPECT_NAME, this::mapEditableSsisPackageProperties);

    mappingHelper.mapToResult(
        INSTITUTIONAL_MEMORY_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setInstitutionalMemory(
                InstitutionalMemoryMapper.map(
                    context, new InstitutionalMemory(dataMap), entityUrn)));

    mappingHelper.mapToResult(
        OWNERSHIP_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setOwnership(
                OwnershipMapper.map(context, new Ownership(dataMap), entityUrn)));

    mappingHelper.mapToResult(
        STATUS_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setStatus(StatusMapper.map(context, new Status(dataMap))));

    mappingHelper.mapToResult(
        GLOBAL_TAGS_ASPECT_NAME,
        (ssisPackage, dataMap) -> this.mapGlobalTags(context, ssisPackage, dataMap, entityUrn));

    mappingHelper.mapToResult(
        GLOSSARY_TERMS_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setGlossaryTerms(
                GlossaryTermsMapper.map(context, new GlossaryTerms(dataMap), entityUrn)));

    mappingHelper.mapToResult(context, DOMAINS_ASPECT_NAME, this::mapDomains);

    mappingHelper.mapToResult(
        DEPRECATION_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setDeprecation(DeprecationMapper.map(context, new Deprecation(dataMap))));

    mappingHelper.mapToResult(
        DATA_PLATFORM_INSTANCE_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setDataPlatformInstance(
                DataPlatformInstanceAspectMapper.map(context, new DataPlatformInstance(dataMap))));

    mappingHelper.mapToResult(
        BROWSE_PATHS_V2_ASPECT_NAME,
        (ssisPackage, dataMap) ->
            ssisPackage.setBrowsePathV2(
                BrowsePathsV2Mapper.map(context, new com.linkedin.common.BrowsePathsV2(dataMap))));

    mappingHelper.mapToResult(
        STRUCTURED_PROPERTIES_ASPECT_NAME,
        ((ssisPackage, dataMap) ->
            ssisPackage.setStructuredProperties(
                StructuredPropertiesMapper.map(context, new StructuredProperties(dataMap)))));

    return mappingHelper.getResult();
  }

  private void mapSsisPackageKey(@Nonnull SsisPackage ssisPackage, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.SsisPackageKey gmsKey = new com.linkedin.ssis.SsisPackageKey(dataMap);

    ssisPackage.setOrchestrator(gmsKey.getOrchestrator());
    ssisPackage.setName(gmsKey.getSsisPackageId());
    ssisPackage.setCluster(gmsKey.getCluster());
    ssisPackage.setPlatform(
        DataPlatform.builder()
            .setType(EntityType.DATA_PLATFORM)
            .setUrn(
                EntityKeyUtils.convertEntityKeyToUrn(
                        new DataPlatformKey().setPlatformName(gmsKey.getOrchestrator()),
                        DATA_PLATFORM_ENTITY_NAME)
                    .toString())
            .build());
  }

  private void mapSsisPackageInfo(@Nonnull SsisPackage ssisPackage, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.SsisPackageInfo gmsProperties =
        new com.linkedin.ssis.SsisPackageInfo(dataMap);
    final com.linkedin.datahub.graphql.generated.SsisPackageInfo properties =
        new com.linkedin.datahub.graphql.generated.SsisPackageInfo();

    properties.setDescription(gmsProperties.getDescription());

    properties.setCreated(
        gmsProperties.getCreated() != null ? gmsProperties.getCreated().getTime() : null);
    properties.setProject(gmsProperties.getProject());

    if (gmsProperties.getName() != null) {
      properties.setName(gmsProperties.getName());
    } else {
      properties.setName(ssisPackage.getName());
    }

    ssisPackage.setDescription(properties.getDescription());
    ssisPackage.setProperties(properties);
  }

  private void mapEditableSsisPackageProperties(
      @Nonnull SsisPackage ssisPackage, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.EditableSsisPackageProperties editableSsisPackageProperties =
        new com.linkedin.ssis.EditableSsisPackageProperties(dataMap);
    final EditableSsisPackageProperties editableProperties = new EditableSsisPackageProperties();

    editableProperties.setDescription(editableSsisPackageProperties.getDescription());
    ssisPackage.setEditableProperties(editableProperties);
  }

  private void mapGlobalTags(
      @Nullable final QueryContext context,
      @Nonnull SsisPackage ssisPackage,
      @Nonnull DataMap dataMap,
      @Nonnull final Urn entityUrn) {
    com.linkedin.datahub.graphql.generated.GlobalTags globalTags =
        GlobalTagsMapper.map(context, new GlobalTags(dataMap), entityUrn);

    ssisPackage.setGlobalTags(globalTags);
  }

  private void mapDomains(
      @Nullable final QueryContext context,
      @Nonnull SsisPackage ssisPackage,
      @Nonnull DataMap dataMap) {
    final Domains domains = new Domains(dataMap);

    ssisPackage.setDomain(DomainAssociationMapper.map(context, domains, ssisPackage.getUrn()));
  }
}
