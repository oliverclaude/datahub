package com.linkedin.datahub.graphql.types.ssis.mappers;

import static com.linkedin.metadata.Constants.*;

import com.linkedin.common.AuditStamp;
import com.linkedin.common.GlobalTags;
import com.linkedin.common.TagAssociationArray;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.template.SetMode;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.generated.SsisControlTaskUpdateInput;
import com.linkedin.datahub.graphql.types.common.mappers.InstitutionalMemoryUpdateMapper;
import com.linkedin.datahub.graphql.types.common.mappers.OwnershipUpdateMapper;
import com.linkedin.datahub.graphql.types.common.mappers.util.UpdateMappingHelper;
import com.linkedin.datahub.graphql.types.mappers.InputModelMapper;
import com.linkedin.datahub.graphql.types.tag.mappers.TagAssociationUpdateMapper;
import com.linkedin.datajob.EditableDataJobProperties;
import com.linkedin.dataset.DatasetDeprecation;
import com.linkedin.mxe.MetadataChangeProposal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SsisControlTaskUpdateInputMapper
    implements InputModelMapper<
        SsisControlTaskUpdateInput, Collection<MetadataChangeProposal>, Urn> {
  public static final SsisControlTaskUpdateInputMapper INSTANCE =
      new SsisControlTaskUpdateInputMapper();

  public static Collection<MetadataChangeProposal> map(
      @Nullable final QueryContext context,
      @Nonnull final SsisControlTaskUpdateInput ssisControlTaskUpdateInput,
      @Nonnull final Urn actor) {
    return INSTANCE.apply(context, ssisControlTaskUpdateInput, actor);
  }

  @Override
  public Collection<MetadataChangeProposal> apply(
      @Nullable final QueryContext context,
      @Nonnull final SsisControlTaskUpdateInput ssisControlTaskUpdateInput,
      @Nonnull final Urn actor) {
    final Collection<MetadataChangeProposal> proposals = new ArrayList<>(3);
    final AuditStamp auditStamp = new AuditStamp();
    auditStamp.setActor(actor, SetMode.IGNORE_NULL);
    auditStamp.setTime(System.currentTimeMillis());
    final UpdateMappingHelper updateMappingHelper = new UpdateMappingHelper(DATA_JOB_ENTITY_NAME);

    if (ssisControlTaskUpdateInput.getOwnership() != null) {
      proposals.add(
          updateMappingHelper.aspectToProposal(
              OwnershipUpdateMapper.map(context, ssisControlTaskUpdateInput.getOwnership(), actor),
              OWNERSHIP_ASPECT_NAME));
    }

    if (ssisControlTaskUpdateInput.getTags() != null) {
      final GlobalTags globalTags = new GlobalTags();

      globalTags.setTags(
          new TagAssociationArray(
              ssisControlTaskUpdateInput.getTags().getTags().stream()
                  .map(element -> TagAssociationUpdateMapper.map(context, element))
                  .collect(Collectors.toList())));

      proposals.add(updateMappingHelper.aspectToProposal(globalTags, TAG_KEY_ASPECT_NAME));
    }

    if (ssisControlTaskUpdateInput.getDeprecation() != null) {
      final DatasetDeprecation deprecation = new DatasetDeprecation();
      deprecation.setDeprecated(ssisControlTaskUpdateInput.getDeprecation().getDeprecated());
      if (ssisControlTaskUpdateInput.getDeprecation().getDecommissionTime() != null) {
        deprecation.setDecommissionTime(
            ssisControlTaskUpdateInput.getDeprecation().getDecommissionTime());
      }
      deprecation.setNote(ssisControlTaskUpdateInput.getDeprecation().getNote());
      deprecation.setActor(actor, SetMode.IGNORE_NULL);
      proposals.add(
          updateMappingHelper.aspectToProposal(deprecation, DATASET_DEPRECATION_ASPECT_NAME));
    }

    if (ssisControlTaskUpdateInput.getInstitutionalMemory() != null) {
      proposals.add(
          updateMappingHelper.aspectToProposal(
              InstitutionalMemoryUpdateMapper.map(
                  context, ssisControlTaskUpdateInput.getInstitutionalMemory()),
              INSTITUTIONAL_MEMORY_ASPECT_NAME));
    }

    if (ssisControlTaskUpdateInput.getEditableProperties() != null) {
      final EditableDataJobProperties editableDataJobProperties = new EditableDataJobProperties();
      editableDataJobProperties.setDescription(
          ssisControlTaskUpdateInput.getEditableProperties().getDescription());
      editableDataJobProperties.setCreated(auditStamp);
      editableDataJobProperties.setLastModified(auditStamp);
      proposals.add(
          updateMappingHelper.aspectToProposal(
              editableDataJobProperties, EDITABLE_DATA_JOB_PROPERTIES_ASPECT_NAME));
    }

    return proposals;
  }
}
