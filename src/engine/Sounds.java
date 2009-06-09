package engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sounds {
	List<SoundClip> clips = new ArrayList<SoundClip>();

	public SoundClip getClipByFileName(String fileName) {
		for (SoundClip c : clips) {
			if (fileName.equals(c.fileName)) {
				return c;
			}
		}
		return null;
	}

	public SoundClip getClipByFileName(String fileName, boolean load) {
		SoundClip ret = this.getClipByFileName(fileName);
		if (ret == null) {
			ret = new SoundClip();
			ret.loadFromFile(fileName);
			clips.add(ret);
		}
		return ret;
	}

	public class SoundClip {
		public Clip clip;
		String fileName;

		void loadFromFile(String fileName) {
			AudioInputStream in = null;
			try {
				in = AudioSystem.getAudioInputStream(new File(fileName));
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			AudioInputStream stream = null;
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			stream = AudioSystem.getAudioInputStream(decodedFormat, in);
			if (stream != null) {
				try {
					clip = AudioSystem.getClip();
					clip.open(stream);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			this.fileName = fileName;
		}
	}
}
