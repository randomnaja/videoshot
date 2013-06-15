package videoshot;

import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChopVideoUtil {

    public static void chopVideo(final long startTimeMicroSec, final long endTimeMicroSec,
            final File sourceMediaFile, final File outputFile) {

        if (!sourceMediaFile.exists()) {
            throw new IllegalArgumentException("Source media is not existed, " + sourceMediaFile.getAbsolutePath());
        }

        final AtomicBoolean isWrite = new AtomicBoolean(false);
        final AtomicBoolean isStopWrite = new AtomicBoolean(false);
        final IMediaReader reader = ToolFactory.makeReader(sourceMediaFile.getAbsolutePath());

        class AddStreamListenner extends MediaToolAdapter {

            @Override
            public void onAddStream(IAddStreamEvent event) {
                super.onAddStream(event);
            }
        }
        final AddStreamListenner addStreamListenner = new AddStreamListenner();

        class VideoListener extends MediaToolAdapter {

            @Override
            public void onVideoPicture(IVideoPictureEvent event) {
                if (event.getTimeStamp() >= startTimeMicroSec && !isWrite.get()) {
                    System.out.println("Write media start from " + event.getTimeStamp());
                    isWrite.set(true);

                    IMediaWriter writer = ToolFactory.makeWriter(outputFile.getAbsolutePath(), reader);
                    this.addListener(writer);
                    writer.addListener(addStreamListenner);
                }

                if (event.getTimeStamp() >= (endTimeMicroSec ) && !isStopWrite.get()) {
                    System.out.println("Write media stop at " + event.getTimeStamp());
                    isStopWrite.set(true);

                    Collection<IMediaListener> allListenner = this.getListeners();
                    for (IMediaListener eachListener : allListenner) {
                        if (eachListener instanceof IMediaWriter) {
                            ((IMediaWriter) eachListener).close();
                        }
                        this.removeListener(eachListener);
                    }
                }

                super.onVideoPicture(event);
            }
        }

        VideoListener videoListener = new VideoListener();

        // reader
        reader.addListener(videoListener);

        // show video when encoding
        //reader.addListener(ToolFactory.makeViewer(true));

        while (reader.readPacket() == null) {
            // continue coding
        }
    }
}
