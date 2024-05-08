package com.linkedin.metadata.kafka.hydrator;

import static com.linkedin.metadata.Constants.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linkedin.data.DataMap;
import com.linkedin.datahub.graphql.types.common.mappers.util.MappingHelper;
import com.linkedin.datajob.DataJobInfo;
import com.linkedin.entity.EntityResponse;
import com.linkedin.entity.EnvelopedAspectMap;
import com.linkedin.ssis.SsisPackageKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SsisPackageHydrator extends BaseHydrator {

  private static final String ORCHESTRATOR = "orchestrator";
  private static final String NAME = "name";

  private static final String SSIS_PACKAGE_ID = "ssisPackageId";

  private static final String CLUSTER = "cluster";

  @Override
  protected void hydrateFromEntityResponse(ObjectNode document, EntityResponse entityResponse) {
    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    MappingHelper<ObjectNode> mappingHelper = new MappingHelper<>(aspectMap, document);
    mappingHelper.mapToResult(
        SSISPACKAGE_INFO_ASPECT_NAME,
        (jsonNodes, dataMap) -> jsonNodes.put(NAME, new DataJobInfo(dataMap).getName()));
    try {
      mappingHelper.mapToResult(SSISPACKAGE_KEY_ASPECT_NAME, this::mapKey);
    } catch (Exception e) {
      log.info("Failed to parse data flow urn for ssis package: {}", entityResponse.getUrn());
    }
  }

  private void mapKey(ObjectNode jsonNodes, DataMap dataMap) {
    SsisPackageKey ssisPackageKey = new SsisPackageKey(dataMap);

    jsonNodes.put(ORCHESTRATOR, ssisPackageKey.getOrchestrator());
    jsonNodes.put(SSIS_PACKAGE_ID, ssisPackageKey.getSsisPackageId());
    jsonNodes.put(CLUSTER, ssisPackageKey.getCluster());
  }
}
