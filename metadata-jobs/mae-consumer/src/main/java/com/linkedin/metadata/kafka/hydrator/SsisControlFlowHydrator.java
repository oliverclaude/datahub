package com.linkedin.metadata.kafka.hydrator;

import static com.linkedin.metadata.Constants.*;
import static com.linkedin.metadata.Constants.SSIS_DATAFLOW_KEY_ASPECT_NAME;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.linkedin.data.DataMap;
import com.linkedin.datahub.graphql.types.common.mappers.util.MappingHelper;
import com.linkedin.datajob.DataJobInfo;
import com.linkedin.entity.EntityResponse;
import com.linkedin.entity.EnvelopedAspectMap;
import com.linkedin.ssis.SsisControlFlowKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SsisControlFlowHydrator extends BaseHydrator {
  private static final String NAME = "name";
  private static final String SSIS_CONTROLFLOW_ID = "controlFlowId";

  @Override
  protected void hydrateFromEntityResponse(ObjectNode document, EntityResponse entityResponse) {
    EnvelopedAspectMap aspectMap = entityResponse.getAspects();
    MappingHelper<ObjectNode> mappingHelper = new MappingHelper<>(aspectMap, document);
    mappingHelper.mapToResult(
        SSIS_DATAFLOW_INFO_ASPECT_NAME,
        (jsonNodes, dataMap) -> jsonNodes.put(NAME, new DataJobInfo(dataMap).getName()));
    try {
      mappingHelper.mapToResult(SSIS_DATAFLOW_KEY_ASPECT_NAME, this::mapKey);
    } catch (Exception e) {
      log.info("Failed to parse data flow urn for ssis package: {}", entityResponse.getUrn());
    }
  }

  private void mapKey(ObjectNode jsonNodes, DataMap dataMap) {
    SsisControlFlowKey ssisControlFlowKey = new SsisControlFlowKey(dataMap);
    jsonNodes.put(SSIS_CONTROLFLOW_ID, ssisControlFlowKey.getControlFlowId());
  }
}
