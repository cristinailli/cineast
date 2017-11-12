package org.vitrivr.cineast.core.data.frames;


import org.vitrivr.cineast.core.data.MultiImage;
import org.vitrivr.cineast.core.decode.subtitle.SubtitleItem;

import java.util.Optional;


/**
 * Represents a single video-frame containing. Such a frame consist of a single image and, optionally, an AudioFrame
 * of arbitrary length.
 *
 * @see AudioFrame
 */
public class VideoFrame {
    public static final VideoFrame EMPTY_VIDEO_FRAME = new VideoFrame(0, 0, MultiImage.EMPTY_MULTIIMAGE, new VideoDescriptor(25, 40, 1, 1));

	/** ID of the VideoFrame. */
  	private final int id;

	/** Timestamp in milliseconds of the VideoFrame relative to the whole file. */
  	private final long timestamp;

	/** MultiImage representing the current VideoFrame. */
	private MultiImage img;

	/** {@link AudioFrame} that is associated with the current frame. May be null! */
	private AudioFrame audioFrame = null;

	/** {@link SubtitleItem} that is associated with the current video frame. May be null! */
	private SubtitleItem subtitleItem = null;

	/** VideoDescriptor that describes the video this frame belongs to. */
	private final VideoDescriptor descriptor;

	/**
	 * Constructor vor VideoFrame.
     *
	 * @param id Incremental ID from the frame (e.g. as returned by the decoder).
	 * @param image Image representing the VideoFrame.
	 */
	public VideoFrame(int id, long timestamp, MultiImage image, VideoDescriptor descriptor){
		this.id = id;
		this.timestamp = timestamp;
		this.img = image;
		this.descriptor = descriptor;
	}

	/**
	 * Getter for ID.
     *
	 * @return ID of the current video frame.
	 */
	public int getId(){
		return this.id;
	}

	/**
	 * Getter for {@link VideoDescriptor}.
	 *
	 * @return {@link VideoDescriptor}
	 */
	public VideoDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * Returns the presentation timestamp of the {@link VideoFrame} in milliseconds.
	 *
	 * @return Presentation timestamp of the {@link VideoFrame}.
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Returns the presentation timestamp of the {@link VideoFrame} in seconds.
	 *
	 * @return Presentation timestamp of the {@link VideoFrame} in seconds.
	 */
	public float getTimestampSeconds() {
		return this.timestamp/1000.0f;
	}

	/**
	 * Getter for frame image.
     *
	 * @return MultiImage representing the current frame.
	 */
	public MultiImage getImage(){
		return this.img;
	}

    /**
     * Getter for frame audio.
     *
     * @return AudioFrame containing the sound of the current frame.
     */
    public final Optional<AudioFrame> getAudio() {
        return Optional.ofNullable(this.audioFrame);
    }

	/**
	 * Getter for subtitle item.
	 *
	 * @return {@link SubtitleItem} associated with current video frame.
	 */
	public final Optional<SubtitleItem> getSubtitleItem() {
    	return Optional.ofNullable(this.subtitleItem);
	}

    /**
     * Adds an AudioFrame to the current VideoFrame. The existing frame (if any)
     * and the new frame are concatenated during the process.
     *
     * @param frame AudioFrame to add to this VideoFrame.
     */
    public void addAudioFrame(AudioFrame frame) {
        if (this.audioFrame == null) {
            this.audioFrame = frame;
        } else {
            this.audioFrame.append(frame);
        }
    }

	/**
	 * Sets the {@link SubtitleItem} for the current frame.
	 *
	 * @param item New {@link SubtitleItem}. Must not be null.
	 */
	public void setSubtitleItem(SubtitleItem item) {
    	this.subtitleItem = item;
	}

    /**
	 * Clears the VideoFrame.
	 */
	public void clear(){
		this.img.clear();
		this.img = null;
		this.audioFrame = null;
	}
}
