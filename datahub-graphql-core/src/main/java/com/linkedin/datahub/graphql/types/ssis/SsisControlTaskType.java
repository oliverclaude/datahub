package com.linkedin.datahub.graphql.types.ssis;

import static com.linkedin.datahub.graphql.Constants.BROWSE_PATH_DELIMITER;
import static com.linkedin.metadata.Constants.*;

import com.datahub.authorization.ConjunctivePrivilegeGroup;
import com.datahub.authorization.DisjunctivePrivilegeGroup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.linkedin.common.urn.CorpuserUrn;
import com.linkedin.common.urn.Urn;
import com.linkedin.common.urn.UrnUtils;
import com.linkedin.data.template.StringArray;
import com.linkedin.datahub.graphql.QueryContext;
import com.linkedin.datahub.graphql.authorization.AuthorizationUtils;
import com.linkedin.datahub.graphql.exception.AuthorizationException;
import com.linkedin.datahub.graphql.generated.*;
import com.linkedin.datahub.graphql.generated.SsisControlTask;
import com.linkedin.datahub.graphql.resolvers.ResolverUtils;
import com.linkedin.datahub.graphql.types.BrowsableEntityType;
import com.linkedin.datahub.graphql.types.MutableType;
import com.linkedin.datahub.graphql.types.SearchableEntityType;
import com.linkedin.datahub.graphql.types.mappers.AutoCompleteResultsMapper;
import com.linkedin.datahub.graphql.types.mappers.BrowsePathsMapper;
import com.linkedin.datahub.graphql.types.mappers.BrowseResultMapper;
import com.linkedin.datahub.graphql.types.mappers.UrnSearchResultsMapper;
import com.linkedin.datahub.graphql.types.ssis.mappers.SsisControlTaskMapper;
import com.linkedin.datahub.graphql.types.ssis.mappers.SsisControlTaskUpdateInputMapper;
import com.linkedin.entity.EntityResponse;
import com.linkedin.entity.client.EntityClient;
import com.linkedin.metadata.Constants;
import com.linkedin.metadata.authorization.PoliciesConfig;
import com.linkedin.metadata.browse.BrowseResult;
import com.linkedin.metadata.query.AutoCompleteResult;
import com.linkedin.metadata.query.filter.Filter;
import com.linkedin.metadata.search.SearchResult;
import com.linkedin.mxe.MetadataChangeProposal;
import com.linkedin.r2.RemoteInvocationException;
import graphql.execution.DataFetcherResult;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SsisControlTaskType
    implements SearchableEntityType<SsisControlTask, String>,
        BrowsableEntityType<SsisControlTask, String>,
        MutableType<SsisControlTaskUpdateInput, SsisControlTask> {

  //  - name: ssisControlTask
  //  category: core
  //  keyAspect: ssisControlTaskKey
  //  aspects:
  //          - domains
  //      - deprecation
  //      - ssisControlTaskInfo
  //      - editableSsisControlTaskProperties
  //      - ssisControlTaskInputOutput
  //      - ownership
  //      - status
  //      - globalTags
  //      - browsePaths
  //      - glossaryTerms
  //      - institutionalMemory
  //      - dataPlatformInstance
  //      - browsePathsV2
  //      - structuredProperties

  private static final Set<String> ASPECTS_TO_RESOLVE =
      ImmutableSet.of(
          DOMAINS_ASPECT_NAME,
          DEPRECATION_ASPECT_NAME,
          SSIS_CONTROLTASK_INFO_ASPECT_NAME,
          SSIS_CONTROLTASK_KEY_ASPECT_NAME,
          EDITABLE_SSIS_CONTROLTASK_PROPERTIES_ASPECT_NAME,
          SSIS_CONTROLTASK_INPUT_OUTPUT_ASPECT_NAME,
          OWNERSHIP_ASPECT_NAME,
          STATUS_ASPECT_NAME,
          GLOBAL_TAGS_ASPECT_NAME,
          BROWSE_PATHS_ASPECT_NAME,
          GLOSSARY_TERMS_ASPECT_NAME,
          INSTITUTIONAL_MEMORY_ASPECT_NAME,
          DATA_PLATFORM_INSTANCE_ASPECT_NAME,
          BROWSE_PATHS_V2_ASPECT_NAME,
          STRUCTURED_PROPERTIES_ASPECT_NAME);

  private static final Set<String> FACET_FIELDS = ImmutableSet.of("cluster", "orchestrator");
  private static final String ENTITY_NAME = "ssisControlTask";

  private final EntityClient _entityClient;

  public SsisControlTaskType(final EntityClient entityClient) {
    _entityClient = entityClient;
  }

  @Override
  public Class<SsisControlTask> objectClass() {
    return SsisControlTask.class;
  }

  @Override
  public Class<SsisControlTaskUpdateInput> inputClass() {
    return SsisControlTaskUpdateInput.class;
  }

  @Override
  public EntityType type() {
    return EntityType.SSIS_CONTROL_TASK;
  }

  @Override
  public Function<Entity, String> getKeyProvider() {
    return Entity::getUrn;
  }

  @Override
  public List<DataFetcherResult<SsisControlTask>> batchLoad(
      @Nonnull final List<String> urnStrs, @Nonnull final QueryContext context) {
    try {
      final List<Urn> urns = urnStrs.stream().map(UrnUtils::getUrn).collect(Collectors.toList());

      final Map<Urn, EntityResponse> ssisControlTaskMap =
          _entityClient.batchGetV2(
              Constants.SSIS_PACKAGE_ENTITY_NAME,
              new HashSet<>(urns),
              ASPECTS_TO_RESOLVE,
              context.getAuthentication());

      final List<EntityResponse> gmsResults = new ArrayList<>();
      for (Urn urn : urns) {
        gmsResults.add(ssisControlTaskMap.getOrDefault(urn, null));
      }
      return gmsResults.stream()
          .map(
              gmsSsisControlTask ->
                  gmsSsisControlTask == null
                      ? null
                      : DataFetcherResult.<SsisControlTask>newResult()
                          .data(SsisControlTaskMapper.map(context, gmsSsisControlTask))
                          .build())
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException("Failed to batch load SSIS control Tasks", e);
    }
  }

  @Override
  public SearchResults search(
      @Nonnull String query,
      @Nullable List<FacetFilterInput> filters,
      int start,
      int count,
      @Nonnull final QueryContext context)
      throws Exception {
    final Map<String, String> facetFilters = ResolverUtils.buildFacetFilters(filters, FACET_FIELDS);
    final SearchResult searchResult =
        _entityClient.search(
            context.getOperationContext().withSearchFlags(flags -> flags.setFulltext(true)),
            ENTITY_NAME,
            query,
            facetFilters,
            start,
            count);
    return UrnSearchResultsMapper.map(context, searchResult);
  }

  @Override
  public AutoCompleteResults autoComplete(
      @Nonnull String query,
      @Nullable String field,
      @Nullable Filter filters,
      int limit,
      @Nonnull final QueryContext context)
      throws Exception {
    final AutoCompleteResult result =
        _entityClient.autoComplete(
            context.getOperationContext(), ENTITY_NAME, query, filters, limit);
    return AutoCompleteResultsMapper.map(context, result);
  }

  @Override
  public BrowseResults browse(
      @Nonnull List<String> path,
      @Nullable List<FacetFilterInput> filters,
      int start,
      int count,
      @Nonnull final QueryContext context)
      throws Exception {
    final Map<String, String> facetFilters = ResolverUtils.buildFacetFilters(filters, FACET_FIELDS);
    final String pathStr =
        path.size() > 0 ? BROWSE_PATH_DELIMITER + String.join(BROWSE_PATH_DELIMITER, path) : "";
    final BrowseResult result =
        _entityClient.browse(
            context.getOperationContext().withSearchFlags(flags -> flags.setFulltext(false)),
            ENTITY_NAME,
            pathStr,
            facetFilters,
            start,
            count);
    return BrowseResultMapper.map(context, result);
  }

  @Override
  public List<BrowsePath> browsePaths(@Nonnull String urn, @Nonnull final QueryContext context)
      throws Exception {
    final StringArray result =
        _entityClient.getBrowsePaths(new Urn(urn), context.getAuthentication());
    return BrowsePathsMapper.map(context, result);
  }

  private boolean isAuthorized(
      @Nonnull String urn,
      @Nonnull SsisControlTaskUpdateInput update,
      @Nonnull QueryContext context) {
    // Decide whether the current principal should be allowed to update the Dataset.
    final DisjunctivePrivilegeGroup orPrivilegeGroups = getAuthorizedPrivileges(update);
    return AuthorizationUtils.isAuthorized(
        context.getAuthorizer(),
        context.getAuthentication().getActor().toUrnStr(),
        PoliciesConfig.SSISPACKAGE_PRIVILEGES.getResourceType(),
        urn,
        orPrivilegeGroups);
  }

  @Override
  public SsisControlTask update(
      @Nonnull String urn, @Nonnull SsisControlTaskUpdateInput input, @Nonnull QueryContext context)
      throws Exception {
    if (isAuthorized(urn, input, context)) {
      final CorpuserUrn actor =
          CorpuserUrn.createFromString(context.getAuthentication().getActor().toUrnStr());
      final Collection<MetadataChangeProposal> proposals =
          SsisControlTaskUpdateInputMapper.map(context, input, actor);
      proposals.forEach(proposal -> proposal.setEntityUrn(UrnUtils.getUrn(urn)));

      try {
        _entityClient.batchIngestProposals(proposals, context.getAuthentication(), false);
      } catch (RemoteInvocationException e) {
        throw new RuntimeException(String.format("Failed to write entity with urn %s", urn), e);
      }

      return load(urn, context).getData();
    }
    throw new AuthorizationException(
        "Unauthorized to perform this action. Please contact your DataHub administrator.");
  }

  private DisjunctivePrivilegeGroup getAuthorizedPrivileges(
      final SsisControlTaskUpdateInput updateInput) {

    final ConjunctivePrivilegeGroup allPrivilegesGroup =
        new ConjunctivePrivilegeGroup(
            ImmutableList.of(
                com.linkedin.metadata.authorization.PoliciesConfig.EDIT_ENTITY_PRIVILEGE
                    .getType()));

    List<String> specificPrivileges = new ArrayList<>();
    if (updateInput.getInstitutionalMemory() != null) {
      specificPrivileges.add(
          com.linkedin.metadata.authorization.PoliciesConfig.EDIT_ENTITY_DOC_LINKS_PRIVILEGE
              .getType());
    }
    if (updateInput.getOwnership() != null) {
      specificPrivileges.add(
          com.linkedin.metadata.authorization.PoliciesConfig.EDIT_ENTITY_OWNERS_PRIVILEGE
              .getType());
    }
    if (updateInput.getDeprecation() != null) {
      specificPrivileges.add(
          com.linkedin.metadata.authorization.PoliciesConfig.EDIT_ENTITY_STATUS_PRIVILEGE
              .getType());
    }
    if (updateInput.getEditableProperties() != null) {
      specificPrivileges.add(
          com.linkedin.metadata.authorization.PoliciesConfig.EDIT_ENTITY_DOCS_PRIVILEGE.getType());
    }

    final ConjunctivePrivilegeGroup specificPrivilegeGroup =
        new ConjunctivePrivilegeGroup(specificPrivileges);

    // If you either have all entity privileges, or have the specific privileges required, you are
    // authorized.
    return new DisjunctivePrivilegeGroup(
        ImmutableList.of(allPrivilegesGroup, specificPrivilegeGroup));
  }
}
