package com.linkedin.datahub.graphql.types.ssis.mappers;

import static com.linkedin.metadata.Constants.*;

import com.linkedin.common.AuditStamp;
import com.linkedin.common.GlobalTags;
import com.linkedin.common.TagAssociationArray;
import com.linkedin.common.urn.Urn;
import com.linkedin.data.template.SetMode;
import com.linkedin.datahub.graphql.generated.SsisDataFlowUpdateInput;
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

public class SsisDataFlowUpdateInputMapper
    implements InputModelMapper<SsisDataFlowUpdateInput, Collection<MetadataChangeProposal>, Urn> {
  public static final SsisDataFlowUpdateInputMapper INSTANCE = new SsisDataFlowUpdateInputMapper();

  public static Collection<MetadataChangeProposal> map(
      @Nonnull final SsisDataFlowUpdateInput ssisDataFlowUpdateInput, @Nonnull final Urn actor) {
    return INSTANCE.apply(ssisDataFlowUpdateInput, actor);
  }

  @Override
  public Collection<MetadataChangeProposal> apply(
      @Nonnull final SsisDataFlowUpdateInput ssisDataFlowUpdateInput, @Nonnull final Urn actor) {
    final Collection<MetadataChangeProposal> proposals = new ArrayList<>(3);
    final UpdateMappingHelper updateMappingHelper = new UpdateMappingHelper(DATA_JOB_ENTITY_NAME);

    final AuditStamp auditStamp = new AuditStamp();
    auditStamp.setActor(actor, SetMode.IGNORE_NULL);
    auditStamp.setTime(System.currentTimeMillis());

    if (ssisDataFlowUpdateInput.getOwnership() != null) {
      proposals.add(
          updateMappingHelper.aspectToProposal(
              OwnershipUpdateMapper.map(ssisDataFlowUpdateInput.getOwnership(), actor),
              OWNERSHIP_ASPECT_NAME));
    }

    if (ssisDataFlowUpdateInput.getTags() != null) {
      final GlobalTags globalTags = new GlobalTags();

      globalTags.setTags(
          new TagAssociationArray(
              ssisDataFlowUpdateInput.getTags().getTags().stream()
                  .map(TagAssociationUpdateMapper::map)
                  .collect(Collectors.toList())));

      proposals.add(updateMappingHelper.aspectToProposal(globalTags, TAG_KEY_ASPECT_NAME));
    }

    if (ssisDataFlowUpdateInput.getDeprecation() != null) {
      final DatasetDeprecation deprecation = new DatasetDeprecation();
      deprecation.setDeprecated(ssisDataFlowUpdateInput.getDeprecation().getDeprecated());
      if (ssisDataFlowUpdateInput.getDeprecation().getDecommissionTime() != null) {
        deprecation.setDecommissionTime(
            ssisDataFlowUpdateInput.getDeprecation().getDecommissionTime());
      }
      deprecation.setNote(ssisDataFlowUpdateInput.getDeprecation().getNote());
      deprecation.setActor(actor, SetMode.IGNORE_NULL);
      proposals.add(
          updateMappingHelper.aspectToProposal(deprecation, DATASET_DEPRECATION_ASPECT_NAME));
    }

    if (ssisDataFlowUpdateInput.getInstitutionalMemory() != null) {
      proposals.add(
          updateMappingHelper.aspectToProposal(
              InstitutionalMemoryUpdateMapper.map(ssisDataFlowUpdateInput.getInstitutionalMemory()),
              INSTITUTIONAL_MEMORY_ASPECT_NAME));
    }

    if (ssisDataFlowUpdateInput.getEditableProperties() != null) {
      final EditableDataJobProperties editableDataJobProperties = new EditableDataJobProperties();
      editableDataJobProperties.setDescription(
          ssisDataFlowUpdateInput.getEditableProperties().getDescription());
      editableDataJobProperties.setCreated(auditStamp);
      editableDataJobProperties.setLastModified(auditStamp);
      proposals.add(
          updateMappingHelper.aspectToProposal(
              editableDataJobProperties, EDITABLE_DATA_JOB_PROPERTIES_ASPECT_NAME));
    }

    return proposals;
  }
}
