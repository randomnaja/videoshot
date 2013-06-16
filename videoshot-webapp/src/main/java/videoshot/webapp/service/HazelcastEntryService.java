package videoshot.webapp.service;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import videoshot.webapp.job.ChopVideoJob;
import videoshot.webapp.job.UploadVideoJob;

@Service
public class HazelcastEntryService implements InitializingBean {

    private HazelcastInstance instance;

    private static final String QUEUE_CHOP_VIDEO = "chopVideoQueue";
    private static final String QUEUE_UPLOAD_VIDEO = "uploadVideoQueue";

    @Override
    public void afterPropertiesSet() throws Exception {
        Config cfg = new Config();
        instance = Hazelcast.newHazelcastInstance(cfg);
    }

    public IQueue<ChopVideoJob> getChopVideoQueue() {
        return instance.getQueue(QUEUE_CHOP_VIDEO);
    }

    public IQueue<UploadVideoJob> getUploadVideoQueue() {
        return instance.getQueue(QUEUE_UPLOAD_VIDEO);
    }
}
