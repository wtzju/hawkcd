package net.hawkengine.services.interfaces;

import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;

public interface IPipelineService extends ICrudService<Pipeline> {
    ServiceResult getAllUnpreparedPipelinesInProgress();

    ServiceResult getAllUpdatedUnpreparedPipelines();

    ServiceResult getAllPreparedPipelinesInProgress();
}
