package com.linkedin.datahub.graphql.types.ssis.mappers;

import static com.linkedin.datahub.graphql.types.ssis.SsisDataFlowType.*;
import static com.linkedin.metadata.Constants.*;
import static com.linkedin.metadata.Constants.STRUCTURED_PROPERTIES_ASPECT_NAME;

import com.google.common.collect.ImmutableList;
import com.linkedin.common.*;
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
import com.linkedin.structured.StructuredProperties;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SsisDataFlowMapper implements ModelMapper<EntityResponse, SsisDataFlow> {

  public static final SsisDataFlowMapper INSTANCE = new SsisDataFlowMapper();

  public static SsisDataFlow map(
      @Nullable final QueryContext context, @Nonnull final EntityResponse ssisDataFlow) {
    return INSTANCE.apply(context, ssisDataFlow);
  }

  public SsisDataFlow apply(
      @Nullable final QueryContext context, @Nonnull final EntityResponse entityResponse) {
    SsisDataFlow result = new SsisDataFlow();
    Urn entityUrn = entityResponse.getUrn();
    result.setUrn(entityResponse.getUrn().toString());
    result.setType(EntityType.SSIS_DATAFLOW);

    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    Long lastIngested = SystemMetadataUtils.getLastIngestedTime(aspectMap);
    result.setLastIngested(lastIngested);

    //    SSIS_DATAFLOW_INFO_ASPECT_NAME,
    //    SSIS_DATAFLOW_KEY_ASPECT_NAME,
    //    EDITABLE_SSIS_DATAFLOW_PROPERTIES_ASPECT_NAME,
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

    //    SSIS_DATAFLOW_INPUT_OUTPUT,
    //    BROWSE_PATHS_ASPECT_NAME,

    MappingHelper<SsisDataFlow> mappingHelper = new MappingHelper<>(aspectMap, result);
    mappingHelper.mapToResult(SSIS_DATAFLOW_KEY_ASPECT_NAME, this::mapSsisDataFlowKey);
    mappingHelper.mapToResult(
        SSIS_DATAFLOW_INFO_ASPECT_NAME,
        (entity, dataMap) -> this.mapSsisDataFlowInfo(entity, dataMap, entityUrn));

    mappingHelper.mapToResult(
        EDITABLE_SSIS_DATAFLOW_PROPERTIES_ASPECT_NAME, this::mapEditableSsisDataFlowProperties);

    mappingHelper.mapToResult(SSIS_DATAFLOW_INPUT_OUTPUT, this::mapDataJobInputOutput);

    mappingHelper.mapToResult(
        INSTITUTIONAL_MEMORY_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setInstitutionalMemory(
                InstitutionalMemoryMapper.map(
                    context, new InstitutionalMemory(dataMap), entityUrn)));
    mappingHelper.mapToResult(
        OWNERSHIP_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setOwnership(
                OwnershipMapper.map(context, new Ownership(dataMap), entityUrn)));
    mappingHelper.mapToResult(
        STATUS_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setStatus(StatusMapper.map(context, new Status(dataMap))));
    mappingHelper.mapToResult(
        GLOBAL_TAGS_ASPECT_NAME,
        (ssisDataFlow, dataMap) -> this.mapGlobalTags(context, ssisDataFlow, dataMap, entityUrn));

    mappingHelper.mapToResult(
        GLOSSARY_TERMS_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setGlossaryTerms(
                GlossaryTermsMapper.map(context, new GlossaryTerms(dataMap), entityUrn)));

    mappingHelper.mapToResult(context, DOMAINS_ASPECT_NAME, this::mapDomains);
    mappingHelper.mapToResult(
        DEPRECATION_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setDeprecation(DeprecationMapper.map(context, new Deprecation(dataMap))));
    mappingHelper.mapToResult(
        DATA_PLATFORM_INSTANCE_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setDataPlatformInstance(
                DataPlatformInstanceAspectMapper.map(context, new DataPlatformInstance(dataMap))));

    mappingHelper.mapToResult(
        BROWSE_PATHS_V2_ASPECT_NAME,
        (ssisDataFlow, dataMap) ->
            ssisDataFlow.setBrowsePathV2(
                BrowsePathsV2Mapper.map(context, new com.linkedin.common.BrowsePathsV2(dataMap))));

    mappingHelper.mapToResult(
        STRUCTURED_PROPERTIES_ASPECT_NAME,
        ((ssisDataFlow, dataMap) ->
            ssisDataFlow.setStructuredProperties(
                StructuredPropertiesMapper.map(context, new StructuredProperties(dataMap)))));

    return mappingHelper.getResult();
  }

  private void mapDataJobInputOutput(SsisDataFlow ssisDataFlow, DataMap dataMap) {
    final com.linkedin.ssis.SsisDataFlowInputOutput gmsSsisDataFlowInputOutput =
        new com.linkedin.ssis.SsisDataFlowInputOutput(dataMap);

    final SsisDataFlowInputOutput result = new SsisDataFlowInputOutput();

    if (gmsSsisDataFlowInputOutput.hasInputDatasets()) {
      result.setInputDatasets(
          gmsSsisDataFlowInputOutput.getInputDatasets().stream()
              .map(
                  urn -> {
                    final Dataset dataset = new Dataset();
                    dataset.setUrn(urn.toString());
                    return dataset;
                  })
              .collect(Collectors.toList()));
    } else {
      result.setInputDatasets(ImmutableList.of());
    }
    if (gmsSsisDataFlowInputOutput.hasOutputDatasets()) {
      result.setOutputDatasets(
          gmsSsisDataFlowInputOutput.getOutputDatasets().stream()
              .map(
                  urn -> {
                    final Dataset dataset = new Dataset();
                    dataset.setUrn(urn.toString());
                    return dataset;
                  })
              .collect(Collectors.toList()));
    } else {
      result.setOutputDatasets(ImmutableList.of());
    }
    if (gmsSsisDataFlowInputOutput.hasInputControlTask()) {
      final SsisControlTask controlTask = new SsisControlTask();
      controlTask.setUrn(gmsSsisDataFlowInputOutput.getInputControlTask().toString());
      result.setInputControlTask(controlTask);
    }

    if (gmsSsisDataFlowInputOutput.hasFineGrainedLineages()
        && gmsSsisDataFlowInputOutput.getFineGrainedLineages() != null) {
      result.setFineGrainedLineages(
          FineGrainedLineagesMapper.map(gmsSsisDataFlowInputOutput.getFineGrainedLineages()));
    }

    ssisDataFlow.setInputOutput(result);
  }

  private void mapSsisDataFlowKey(@Nonnull SsisDataFlow ssisDataFlow, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.SsisDataFlowKey gmsKey = new com.linkedin.ssis.SsisDataFlowKey(dataMap);

    ssisDataFlow.setName(gmsKey.getDataFlowId());

    ssisDataFlow.setSsisPackage(
        new SsisPackage.Builder().setUrn(gmsKey.getSsisPackage().toString()).build());
  }

  private void mapSsisDataFlowInfo(
      @Nonnull SsisDataFlow ssisDataFlow, @Nonnull DataMap dataMap, @Nonnull Urn entityUrn) {
    final com.linkedin.ssis.SsisDataFlowInfo gmsProperties =
        new com.linkedin.ssis.SsisDataFlowInfo(dataMap);
    final com.linkedin.datahub.graphql.generated.SsisDataFlowInfo properties =
        new com.linkedin.datahub.graphql.generated.SsisDataFlowInfo();
    properties.setDescription(gmsProperties.getDescription());
    properties.setName(gmsProperties.getName());

    if (gmsProperties.getCreated() != null)
      properties.setCreated(gmsProperties.getCreated().getTime());

    if (gmsProperties.getName() != null) {
      properties.setName(gmsProperties.getName());

    } else {
      properties.setName(ssisDataFlow.getName());
    }
    ssisDataFlow.setName(ssisDataFlow.getName());
    ssisDataFlow.setDescription(properties.getDescription());
    ssisDataFlow.setProperties(properties);
  }

  private void mapEditableSsisDataFlowProperties(
      @Nonnull SsisDataFlow ssisDataFlow, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.EditableSsisDataFlowProperties editableSsisDataFlowProperties =
        new com.linkedin.ssis.EditableSsisDataFlowProperties(dataMap);
    final EditableSsisDataFlowProperties editableProperties = new EditableSsisDataFlowProperties();
    editableProperties.setDescription(editableSsisDataFlowProperties.getDescription());
    ssisDataFlow.setEditableProperties(editableProperties);
  }

  private void mapGlobalTags(
      @Nullable final QueryContext context,
      @Nonnull SsisDataFlow ssisDataFlow,
      @Nonnull DataMap dataMap,
      @Nonnull final Urn entityUrn) {
    com.linkedin.datahub.graphql.generated.GlobalTags globalTags =
        GlobalTagsMapper.map(context, new GlobalTags(dataMap), entityUrn);
    ssisDataFlow.setGlobalTags(globalTags);
  }

  private void mapDomains(
      @Nullable final QueryContext context,
      @Nonnull SsisDataFlow ssisDataFlow,
      @Nonnull DataMap dataMap) {
    final Domains domains = new Domains(dataMap);
    ssisDataFlow.setDomain(DomainAssociationMapper.map(context, domains, ssisDataFlow.getUrn()));
  }
}
