package com.linkedin.datahub.graphql.types.ssis.mappers;

import static com.linkedin.datahub.graphql.types.ssis.SsisControlTaskType.*;
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

public class SsisControlTaskMapper implements ModelMapper<EntityResponse, SsisControlTask> {

  public static final SsisControlTaskMapper INSTANCE = new SsisControlTaskMapper();

  public static SsisControlTask map(
      @Nullable final QueryContext context, @Nonnull final EntityResponse ssisControlTask) {
    return INSTANCE.apply(context, ssisControlTask);
  }

  public SsisControlTask apply(
      @Nullable final QueryContext context, @Nonnull final EntityResponse entityResponse) {
    SsisControlTask result = new SsisControlTask();
    Urn entityUrn = entityResponse.getUrn();
    result.setUrn(entityResponse.getUrn().toString());
    result.setType(EntityType.SSIS_CONTROL_TASK);

    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    Long lastIngested = SystemMetadataUtils.getLastIngestedTime(aspectMap);
    result.setLastIngested(lastIngested);

    //    SSIS_CONTROLTASK_INFO_ASPECT_NAME,
    //    SSIS_CONTROLTASK_KEY_ASPECT_NAME,
    //    EDITABLE_SSIS_CONTROLTASK_PROPERTIES_ASPECT_NAME,
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

    //    SSIS_CONTROLTASK_INPUT_OUTPUT_ASPECT_NAME,
    //    BROWSE_PATHS_ASPECT_NAME,

    MappingHelper<SsisControlTask> mappingHelper = new MappingHelper<>(aspectMap, result);
    mappingHelper.mapToResult(SSIS_CONTROLTASK_KEY_ASPECT_NAME, this::mapSsisControlTaskKey);
    mappingHelper.mapToResult(
        SSIS_CONTROLTASK_INFO_ASPECT_NAME,
        (entity, dataMap) -> this.mapSsisControlTaskInfo(entity, dataMap, entityUrn));

    mappingHelper.mapToResult(
        EDITABLE_SSIS_CONTROLTASK_PROPERTIES_ASPECT_NAME,
        this::mapEditableSsisControlTaskProperties);

    mappingHelper.mapToResult(
        SSIS_CONTROLTASK_INPUT_OUTPUT_ASPECT_NAME, this::mapDontrolTaskInputOutput);

    mappingHelper.mapToResult(
        INSTITUTIONAL_MEMORY_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setInstitutionalMemory(
                InstitutionalMemoryMapper.map(
                    context, new InstitutionalMemory(dataMap), entityUrn)));
    mappingHelper.mapToResult(
        OWNERSHIP_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setOwnership(
                OwnershipMapper.map(context, new Ownership(dataMap), entityUrn)));
    mappingHelper.mapToResult(
        STATUS_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setStatus(StatusMapper.map(context, new Status(dataMap))));
    mappingHelper.mapToResult(
        GLOBAL_TAGS_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            this.mapGlobalTags(context, ssisControlTask, dataMap, entityUrn));

    mappingHelper.mapToResult(
        GLOSSARY_TERMS_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setGlossaryTerms(
                GlossaryTermsMapper.map(context, new GlossaryTerms(dataMap), entityUrn)));

    mappingHelper.mapToResult(context, DOMAINS_ASPECT_NAME, this::mapDomains);
    mappingHelper.mapToResult(
        DEPRECATION_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setDeprecation(
                DeprecationMapper.map(context, new Deprecation(dataMap))));
    mappingHelper.mapToResult(
        DATA_PLATFORM_INSTANCE_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setDataPlatformInstance(
                DataPlatformInstanceAspectMapper.map(context, new DataPlatformInstance(dataMap))));

    mappingHelper.mapToResult(
        BROWSE_PATHS_V2_ASPECT_NAME,
        (ssisControlTask, dataMap) ->
            ssisControlTask.setBrowsePathV2(
                BrowsePathsV2Mapper.map(context, new com.linkedin.common.BrowsePathsV2(dataMap))));

    mappingHelper.mapToResult(
        STRUCTURED_PROPERTIES_ASPECT_NAME,
        ((ssisControlTask, dataMap) ->
            ssisControlTask.setStructuredProperties(
                StructuredPropertiesMapper.map(context, new StructuredProperties(dataMap)))));

    return mappingHelper.getResult();
  }

  private void mapSsisControlTaskKey(
      @Nonnull SsisControlTask ssisControlTask, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.SsisControlTaskKey gmsKey =
        new com.linkedin.ssis.SsisControlTaskKey(dataMap);

    ssisControlTask.setName(gmsKey.getControlTaskId());
    ssisControlTask.setFlow(
        new SsisControlFlow.Builder().setUrn(gmsKey.getFlow().toString()).build());
  }

  private void mapDontrolTaskInputOutput(SsisControlTask ssisControlTask, DataMap dataMap) {
    final com.linkedin.ssis.SsisControlTaskInputOutput gmsSsisDControlTaskInputOutput =
        new com.linkedin.ssis.SsisControlTaskInputOutput(dataMap);

    final SsisControlTaskInputOutput result = new SsisControlTaskInputOutput();

    if (gmsSsisDControlTaskInputOutput.hasInputDatasets()) {
      result.setInputDatasets(
          gmsSsisDControlTaskInputOutput.getInputDatasets().stream()
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
    if (gmsSsisDControlTaskInputOutput.hasOutputDatasets()) {
      result.setOutputDatasets(
          gmsSsisDControlTaskInputOutput.getOutputDatasets().stream()
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

    if (gmsSsisDControlTaskInputOutput.hasInputControlTasks()) {
      result.setInputControlTasks(
          gmsSsisDControlTaskInputOutput.getInputControlTasks().stream()
              .map(
                  urn -> {
                    final SsisControlTask controlTask = new SsisControlTask();
                    controlTask.setUrn(urn.toString());
                    return controlTask;
                  })
              .collect(Collectors.toList()));
    } else {
      result.setOutputDatasets(ImmutableList.of());
    }

    if (gmsSsisDControlTaskInputOutput.hasContainingControlTask()) {
      final SsisControlTask controlTask = new SsisControlTask();
      controlTask.setUrn(gmsSsisDControlTaskInputOutput.getContainingControlTask().toString());
      result.setContainerControlTask(controlTask);
    }

    ssisControlTask.setInputOutput(result);
  }

  private void mapSsisControlTaskInfo(
      @Nonnull SsisControlTask ssisControlTask, @Nonnull DataMap dataMap, @Nonnull Urn entityUrn) {
    final com.linkedin.ssis.SsisControlTaskInfo gmsProperties =
        new com.linkedin.ssis.SsisControlTaskInfo(dataMap);
    final com.linkedin.datahub.graphql.generated.SsisControlTaskInfo properties =
        new com.linkedin.datahub.graphql.generated.SsisControlTaskInfo();
    properties.setDescription(gmsProperties.getDescription());
    properties.setName(gmsProperties.getName());

    if (gmsProperties.getCreated() != null)
      properties.setCreated(gmsProperties.getCreated().getTime());

    if (gmsProperties.getName() != null) {
      properties.setName(gmsProperties.getName());
    } else {
      properties.setName(ssisControlTask.getName());
    }

    ssisControlTask.setDescription(properties.getDescription());
    ssisControlTask.setProperties(properties);
  }

  private void mapEditableSsisControlTaskProperties(
      @Nonnull SsisControlTask ssisControlTask, @Nonnull DataMap dataMap) {
    final com.linkedin.ssis.EditableSsisControlTaskProperties editableSsisControlTaskProperties =
        new com.linkedin.ssis.EditableSsisControlTaskProperties(dataMap);
    final EditableSsisControlTaskProperties editableProperties =
        new EditableSsisControlTaskProperties();
    editableProperties.setDescription(editableSsisControlTaskProperties.getDescription());
    ssisControlTask.setEditableProperties(editableProperties);
  }

  private void mapGlobalTags(
      @Nullable final QueryContext context,
      @Nonnull SsisControlTask ssisControlTask,
      @Nonnull DataMap dataMap,
      @Nonnull final Urn entityUrn) {
    com.linkedin.datahub.graphql.generated.GlobalTags globalTags =
        GlobalTagsMapper.map(context, new GlobalTags(dataMap), entityUrn);
    ssisControlTask.setGlobalTags(globalTags);
  }

  private void mapDomains(
      @Nullable final QueryContext context,
      @Nonnull SsisControlTask ssisControlTask,
      @Nonnull DataMap dataMap) {
    final Domains domains = new Domains(dataMap);
    ssisControlTask.setDomain(
        DomainAssociationMapper.map(context, domains, ssisControlTask.getUrn()));
  }
}
