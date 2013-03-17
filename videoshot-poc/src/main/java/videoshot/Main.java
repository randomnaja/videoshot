package videoshot;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IReadPacketEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.IWritePacketEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.demos.VideoImage;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class Main {

    static AtomicBoolean updateFile = new AtomicBoolean(false);

    public static void main3(String[] args) throws Exception {
//        // create a media reader
//        IMediaReader reader = ToolFactory.makeReader();
//
//        // create a writer which receives the decoded media from
//        // reader, encodes it and writes it out to the specified file
//        IMediaWriter writer = ToolFactory.makeWriter("/tmp/output.mp4", reader);
//
//        // add a debug listener to the writer to see media writer events
//        //reader.addListener(writer);
////        reader.addListener(new MediaListenerAdapter(){
////            @Override
////            public void onReadPacket(IReadPacketEvent event) {
////                System.out.println("Read packet, " + event.getPacket().getTimeStamp());
////            }
////        });
//
//        // read and decode packets from the source file and
//        // then encode and write out data to the output file
//        while (reader.readPacket() != null) {
//
//        }

        final IMediaReader reader = ToolFactory.makeReader("/home/tone/ENC/ToneDropbox/Dropbox/small.mp4");

        class CutChecker extends MediaToolAdapter {

            @Override
            public void onVideoPicture(IVideoPictureEvent event) {
                if (event.getTimeStamp() >= 4000000L && !updateFile.get()) {
                    System.out.println("write");
                    updateFile.set(true);
                    this.addListener(ToolFactory.makeWriter("/tmp/cut.mp4", reader));
                }
                System.out.println("ON video " + event.getTimeStamp());
            }

            @Override
            public void onAddStream(IAddStreamEvent event) {
//                int streamIndex = event.getStreamIndex();
//                IStreamCoder streamCoder = event.getSource().getContainer().getStream(streamIndex).getStreamCoder();
//                if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
//                } else if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
//                    streamCoder.setWidth(300);
//                    streamCoder.setHeight(200);
//                }
                super.onAddStream(event);
            }
        }
        final CutChecker cutChecker = new CutChecker();
        reader.addListener(cutChecker);

        reader.addListener(ToolFactory.makeViewer(true));
        //IMediaWriter writer = ToolFactory.makeWriter("/tmp/cut.mp4", reader);
        //cutChecker.addListener(writer);

        while (reader.readPacket() == null){
//            if ((cutChecker.timeInMilisec >= 2 * 1000000) && (!updated)){
//                cutChecker.removeListener(writer);
//                writer.close();
//                writer = ToolFactory.makeWriter(file2_conv_part2, reader);
//                cutChecker.addListener(writer);
//                updated = true;
//            }
        }
    }


    public static void main4(String[] args) throws Exception {
        // create custom listeners
        MyVideoListener myVideoListener = new MyVideoListener(300, 200);
        Resizer resizer = new Resizer(300, 200);

        // reader
        IMediaReader reader = ToolFactory.makeReader("/home/tone/ENC/ToneDropbox/Dropbox/small.mp4");
        reader.addListener(resizer);

        // writer
        IMediaWriter writer = ToolFactory.makeWriter("/tmp/cut.mp4", reader);
        resizer.addListener(writer);
        writer.addListener(myVideoListener);

        // show video when encoding
        //reader.addListener(ToolFactory.makeViewer(true));

        while (reader.readPacket() == null) {
            // continue coding
        }
    }

    public static void mainWORK(String[] args) throws Exception {

        final AtomicBoolean isWrite = new AtomicBoolean(false);
        final long PERIOD1SECOND = 1000000;
        final AtomicLong lastPeriod = new AtomicLong(PERIOD1SECOND);
        //final IMediaReader reader = ToolFactory.makeReader("/home/tone/ToneDropbox/Dropbox/small.mp4");
        final IMediaReader reader = ToolFactory.makeReader("/home/tone/Musics/MV/Zidane All in the touch - Hala Madrid I.mp4");

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
                if (event.getTimeStamp() >= lastPeriod.get() && event.getImage() != null) {
                    lastPeriod.set(lastPeriod.get() + PERIOD1SECOND);

                    System.out.println("Write image " + event.getTimeStamp());
                    BufferedImage image = event.getImage();
                    try {
                        Sanselan.writeImage(image, new File("/tmp/ss" + event.getTimeStamp() + ".png"),
                                ImageFormat.IMAGE_FORMAT_PNG, null);
                    } catch (ImageWriteException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (event.getTimeStamp() >= 4000000L && !isWrite.get()) {
                    isWrite.set(true);

                    IMediaWriter writer = ToolFactory.makeWriter("/tmp/cut.mp4", reader);
                    this.addListener(writer);
                    writer.addListener(addStreamListenner);
                }
                super.onVideoPicture(event);
            }
        }

        VideoListener videoListener = new VideoListener();

        // reader
        reader.addListener(videoListener);

//        IMediaWriter writer = ToolFactory.makeWriter("/tmp/cut.mp4", reader);
//        videoListener.addListener(writer);
//        writer.addListener(addStreamListenner);

        // show video when encoding
        //reader.addListener(ToolFactory.makeViewer(true));

        while (reader.readPacket() == null) {
            // continue coding
        }
    }

    static class Resizer extends MediaToolAdapter {
        private Integer width;
        private Integer height;

        private IVideoResampler videoResampler = null;

        public Resizer(Integer aWidth, Integer aHeight) {
            this.width = aWidth;
            this.height = aHeight;
        }

        @Override
        public void onVideoPicture(IVideoPictureEvent event) {
            if (event.getTimeStamp() >= 4000000L && !updateFile.get()) {
                System.out.println("write");
                updateFile.set(true);
                //this.addListener(ToolFactory.makeWriter("/tmp/cut.mp4", reader));
            }
            System.out.println("ON video " + event.getTimeStamp());

//            IVideoPicture pic = event.getPicture();
//            if (videoResampler == null) {
//                videoResampler = IVideoResampler.make(width, height, pic.getPixelType(), pic.getWidth(), pic
//                        .getHeight(), pic.getPixelType());
//            }
//            IVideoPicture out = IVideoPicture.make(pic.getPixelType(), width, height);
//            videoResampler.resample(out, pic);
//
//            IVideoPictureEvent asc = new VideoPictureEvent(event.getSource(), out, event.getStreamIndex());
//            super.onVideoPicture(asc);
//            out.delete();
            super.onVideoPicture(event);
        }
    }

    static class MyVideoListener extends MediaToolAdapter {
        private Integer width;
        private Integer height;

        public MyVideoListener(Integer aWidth, Integer aHeight) {
            this.width = aWidth;
            this.height = aHeight;
        }

        @Override
        public void onAddStream(IAddStreamEvent event) {
//            int streamIndex = event.getStreamIndex();
//            IStreamCoder streamCoder = event.getSource().getContainer().getStream(streamIndex).getStreamCoder();
//            if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
//            } else if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
//                streamCoder.setWidth(width);
//                streamCoder.setHeight(height);
//            }
            super.onAddStream(event);
        }

    }

    @SuppressWarnings("deprecation")
    public static void main1(String[] args) throws IOException, ImageWriteException {
        String filename = "/home/tone/Musics/MV/Zidane All in the touch - Hala Madrid I.mp4";

        // Let's make sure that we can actually convert video pixel formats.
        if (!IVideoResampler.isSupported(
                IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
            throw new RuntimeException("you must install the GPL version" +
                    " of Xuggler (with IVideoResampler support) for " +
                    "this demo to work");
        }

        // Create a Xuggler container object
        IContainer container = IContainer.make();

        // Open up the container
        if (container.open(filename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: " + filename);
        }

        // query how many streams the call to open found
        int numStreams = container.getNumStreams();

        // and iterate through the streams to find the first video stream
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;
        for (int i = 0; i < numStreams; i++) {
            // Find the stream object
            IStream stream = container.getStream(i);
            // Get the pre-configured decoder that can decode this stream;
            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        if (videoStreamId == -1) {
            throw new RuntimeException("could not find video stream in container: "
                    + filename);
        }

    /*
     * Now we have found the video stream in this file.  Let's open up our decoder so it can
     * do work.
     */
        if (videoCoder.open() < 0) {
            throw new RuntimeException("could not open video decoder for container: "
                    + filename);
        }

        IVideoResampler resampler = null;
        if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
            // if this stream is not in BGR24, we're going to need to
            // convert it.  The VideoResampler does that for us.
            resampler = IVideoResampler.make(videoCoder.getWidth(),
                    videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                    videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
            if (resampler == null) {
                throw new RuntimeException("could not create color space " +
                        "resampler for: " + filename);
            }
        }
    /*
     * And once we have that, we draw a window on screen
     */
        openJavaWindow();

    /*
     * Now, we start walking through the container looking at each packet.
     */
        IPacket packet = IPacket.make();
        long firstTimestampInStream = Global.NO_PTS;
        long systemClockStartTime = 0;
        long PERIODSECOND = 1000000L;
        long nextPeriod = PERIODSECOND;

        while (container.readNextPacket(packet) >= 0) {
      /*
       * Now we have a packet, let's see if it belongs to our video stream
       */
            if (packet.getStreamIndex() == videoStreamId) {
        /*
         * We allocate a new picture to get the data out of Xuggler
         */
                IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                        videoCoder.getWidth(), videoCoder.getHeight());

                int offset = 0;
                while (offset < packet.getSize()) {
          /*
           * Now, we decode the video, checking for any errors.
           *
           */
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
                    if (bytesDecoded < 0) {
                        throw new RuntimeException("got error decoding video in: "
                                + filename);
                    }
                    offset += bytesDecoded;

          /*
           * Some decoders will consume data in a packet, but will not be able to construct
           * a full video picture yet.  Therefore you should always check if you
           * got a complete picture from the decoder
           */
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
            /*
             * If the resampler is not null, that means we didn't get the
             * video in BGR24 format and
             * need to convert it into BGR24 format.
             */
                        if (resampler != null) {
                            // we must resample
                            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
                                    picture.getWidth(), picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException("could not resample video from: "
                                        + filename);
                            }
                        }
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException("could not decode video" +
                                    " as BGR 24 bit data in: " + filename);
                        }

                        /**
                         * We could just display the images as quickly as we decode them,
                         * but it turns out we can decode a lot faster than you think.
                         *
                         * So instead, the following code does a poor-man's version of
                         * trying to match up the frame-rate requested for each
                         * IVideoPicture with the system clock time on your computer.
                         *
                         * Remember that all Xuggler IAudioSamples and IVideoPicture objects
                         * always give timestamps in Microseconds, relative to the first
                         * decoded item. If instead you used the packet timestamps, they can
                         * be in different units depending on your IContainer, and IStream
                         * and things can get hairy quickly.
                         */
                        if (firstTimestampInStream == Global.NO_PTS) {
                            // This is our first time through
                            firstTimestampInStream = picture.getTimeStamp();
                            // get the starting clock time so we can hold up frames
                            // until the right time.
                            systemClockStartTime = System.currentTimeMillis();
                        } else {
                            long systemClockCurrentTime = System.currentTimeMillis();
                            long millisecondsClockTimeSinceStartofVideo =
                                    systemClockCurrentTime - systemClockStartTime;
                            // compute how long for this frame since the first frame in the
                            // stream.
                            // remember that IVideoPicture and IAudioSamples timestamps are
                            // always in MICROSECONDS,
                            // so we divide by 1000 to get milliseconds.
                            long millisecondsStreamTimeSinceStartOfVideo =
                                    (picture.getTimeStamp() - firstTimestampInStream) / 1000;
                            final long millisecondsTolerance = 50; // and we give ourselfs 50 ms of tolerance
                            final long millisecondsToSleep =
                                    (millisecondsStreamTimeSinceStartOfVideo -
                                            (millisecondsClockTimeSinceStartofVideo +
                                                    millisecondsTolerance));
                            if (millisecondsToSleep > 0) {
                                try {
                                    Thread.sleep(millisecondsToSleep);
                                } catch (InterruptedException e) {
                                    // we might get this when the user closes the dialog box, so
                                    // just return from the method.
                                    return;
                                }
                            }
                        }

                        System.out.println("Got image, timestamp = " + picture.getTimeStamp() + ", " + picture.getFormattedTimeStamp());

                        if (picture.getTimeStamp() > nextPeriod) {
                            nextPeriod += PERIODSECOND;
                            System.out.println("Print image of time : " + picture.getFormattedTimeStamp());
                            // And finally, convert the BGR24 to an Java buffered image
                            BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                            Sanselan.writeImage(javaImage, new File("/tmp/ss_" + picture.getFormattedTimeStamp() + ".png"),
                                    ImageFormat.IMAGE_FORMAT_PNG, new HashMap());
                        }

                        // and display it on the Java Swing window
                        //updateJavaWindow(javaImage);
                    }
                }
            } else {
        /*
         * This packet isn't part of our video stream, so we just
         * silently drop it.
         */
                do {
                } while (false);
            }

        }
    /*
     * Technically since we're exiting anyway, these will be cleaned up by
     * the garbage collector... but because we're nice people and want
     * to be invited places for Christmas, we're going to show how to clean up.
     */
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }
        closeJavaWindow();

    }

    /**
     * The window we'll draw the video on.
     */
    private static VideoImage mScreen = null;

    private static void updateJavaWindow(BufferedImage javaImage) {
        mScreen.setImage(javaImage);
    }

    /**
     * Opens a Swing window on screen.
     */
    private static void openJavaWindow() {
        mScreen = new VideoImage();
    }

    /**
     * Forces the swing thread to terminate; I'm sure there is a right way to do this in swing, but this works too.
     */
    private static void closeJavaWindow() {
        System.exit(0);
    }
}
