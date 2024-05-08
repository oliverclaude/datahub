package com.linkedin.datahub.graphql.types.ssis.mappers;

import static com.linkedin.datahub.graphql.types.ssis.SsisControlFlowType.*;
import static com.linkedin.metadata.Constants.*;
import static com.linkedin.metadata.Constants.STRUCTURED_PROPERTIES_ASPECT_NAME;

import com.linkedin.common.*;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.DataMap;
import com.linkedin.datahub.graphql.generated.EditableSsisControlFlowProperties;
import com.linkedin.datahub.graphql.generated.EntityType;
import com.linkedin.datahub.graphql.generated.SsisControlFlow;
import com.linkedin.datahub.graphql.generated.SsisPackage;
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
import com.linkedin.structured.StructuredProperties;
import javax.annotation.Nonnull;

public class SsisControlFlowMapper implements ModelMapper<EntityResponse, SsisControlFlow> {

  public static final SsisControlFlowMapper INSTANCE = new SsisControlFlowMapper();

  public static SsisControlFlow map(@Nonnull final EntityResponse ssisControlFlow) {
    return INSTANCE.apply(ssisControlFlow);
  }

  public SsisControlFlow apply(@Nonnull final EntityResponse entityResponse) {
    SsisControlFlow result = new SsisControlFlow();
    Urn entityUrn = entityResponse.getUrn();
    result.setUrn(entityResponse.getUrn().toString());
    result.setType(EntityType.SSIS_CONTROL_FLOW);

    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    Long lastIngested = SystemMetadataUtils.getLastIngestedTime(aspectMap);
    result.setLastIngested(lastIngested);

    //    SSIS_CONTROLFLOW_INFO_ASPECT_NAME,
    //    SSIS_CONTROLFLOW_KEY_ASPECT_NAME,
    //    EDITABLE_SSIS_CONTROLFLOW_PROPERTIES_ASPECT_NAME,
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

    MappingHelper<SsisControlFlow> mappingHelper = new MappingHelper<>(aspectMap, result);
    mappingHelper.mapToResult(SSIS_CONTROLFLOW_KEY_ASPECT_NAME, this::mapSsisControlFlowKey);
    mappingHelper.mapToResult(
        SSIS_CONTROLFLOW_INFO_ASPECT_NAME,
        (entity, dataMap) -> this.mapSsisControlFlowInfo(entity, dataMap, entityUrn));

    mappingHelper.mapToResult(
        EDITABLE_SSIS_CONTROLFLOW_PROPERTIES_ASPECT_NAME,
        this::mapEditableSsisControlFlowProperties);

    mappingHelper.mapToResult(
        INSTITUTIONAL_MEMORY_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setInstitutionalMemory(
                InstitutionalMemoryMapper.map(new InstitutionalMemory(dataMap), entityUrn)));
    mappingHelper.mapToResult(
        OWNERSHIP_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setOwnership(OwnershipMapper.map(new Ownership(dataMap), entityUrn)));
    mappingHelper.mapToResult(
        STATUS_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setStatus(StatusMapper.map(new Status(dataMap))));
    mappingHelper.mapToResult(
        GLOBAL_TAGS_ASPECT_NAME,
        (ssisControlFlow, dataMap) -> this.mapGlobalTags(ssisControlFlow, dataMap, entityUrn));

    mappingHelper.mapToResult(
        GLOSSARY_TERMS_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setGlossaryTerms(
                GlossaryTermsMapper.map(new GlossaryTerms(dataMap), entityUrn)));

    mappingHelper.mapToResult(DOMAINS_ASPECT_NAME, this::mapDomains);
    mappingHelper.mapToResult(
        DEPRECATION_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setDeprecation(DeprecationMapper.map(new Deprecation(dataMap))));
    mappingHelper.mapToResult(
        DATA_PLATFORM_INSTANCE_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setDataPlatformInstance(
                DataPlatformInstanceAspectMapper.map(new DataPlatformInstance(dataMap))));

    mappingHelper.mapToResult(
        BROWSE_PATHS_V2_ASPECT_NAME,
        (ssisControlFlow, dataMap) ->
            ssisControlFlow.setBrowsePathV2(
                BrowsePathsV2Mapper.map(new com.linkedin.common.BrowsePathsV2(dataMap))));

    mappingHelper.mapToResult(
        STRUCTURED_PROPERTIES_ASPECT_NAME,
        ((ssisControlFlow, dataMap) ->
            ssisControlFlow.setStructuredProperties(
                StructuredPropertiesMapper.map(new StructuredProperties(dataMap)))));

    return mappingHelper.getResult();
  }

  private void mapSsisControlFlowKey(
      @Nonnull SsisControlFlow ssisControlFlow, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.SsisControlFlowKey gmsKey =
        new com.linkedin.ssis.SsisControlFlowKey(dataMap);

    ssisControlFlow.setName(gmsKey.getControlFlowId());
    ssisControlFlow.setSsisPackage(
        new SsisPackage.Builder().setUrn(gmsKey.getSsisPackage().toString()).build());
  }

  private void mapSsisControlFlowInfo(
      @Nonnull SsisControlFlow ssisControlFlow, @Nonnull DataMap dataMap, @Nonnull Urn entityUrn) {
    final com.linkedin.ssis.SsisControlFlowInfo gmsProperties =
        new com.linkedin.ssis.SsisControlFlowInfo(dataMap);
    final com.linkedin.datahub.graphql.generated.SsisControlFlowInfo properties =
        new com.linkedin.datahub.graphql.generated.SsisControlFlowInfo();
    properties.setDescription(gmsProperties.getDescription());
    if (gmsProperties.getCreated() != null)
      properties.setCreated(gmsProperties.getCreated().getTime());
    properties.setProject(gmsProperties.getProject());

    if (gmsProperties.getName() != null) {
      properties.setName(gmsProperties.getName());

    } else {
      properties.setName(ssisControlFlow.getName());
    }

    ssisControlFlow.setDescription(properties.getDescription());
    ssisControlFlow.setProperties(properties);
  }

  private void mapEditableSsisControlFlowProperties(
      @Nonnull SsisControlFlow ssisControlFlow, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.EditableSsisControlFlowProperties editableSsisControlFlowProperties =
        new com.linkedin.ssis.EditableSsisControlFlowProperties(dataMap);
    final EditableSsisControlFlowProperties editableProperties =
        new EditableSsisControlFlowProperties();
    editableProperties.setDescription(editableSsisControlFlowProperties.getDescription());
    ssisControlFlow.setEditableProperties(editableProperties);
  }

  private void mapGlobalTags(
      @Nonnull SsisControlFlow ssisControlFlow,
      @Nonnull DataMap dataMap,
      @Nonnull final Urn entityUrn) {
    com.linkedin.datahub.graphql.generated.GlobalTags globalTags =
        GlobalTagsMapper.map(new GlobalTags(dataMap), entityUrn);
    ssisControlFlow.setGlobalTags(globalTags);
  }

  private void mapDomains(@Nonnull SsisControlFlow ssisControlFlow, @Nonnull DataMap dataMap) {
    final Domains domains = new Domains(dataMap);
    ssisControlFlow.setDomain(DomainAssociationMapper.map(domains, ssisControlFlow.getUrn()));
  }
}
