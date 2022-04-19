package jade;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound {
    private int bufferId;
    private int sourceId;
    // Filepath to .ogg sound file
    private String filepath;

    private boolean isPlaying = false;

    public Sound(String filepath, boolean loops) {
        this.filepath = filepath;

        // Use STB to load audio file
        // Allocate space to store the audio from STB
        stackPush();
        IntBuffer channelsBuffer = stackMallocInt(1);
        stackPush();
        IntBuffer sampleRateBuffer = stackMallocInt(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(filepath, channelsBuffer, sampleRateBuffer);
        // Check if the AudioBuffer was created successfully
        if (rawAudioBuffer == null) {
            System.out.println("Could not load sound " + filepath);
            stackPop();
            stackPop();
            return;
        }

        // Retrieve additional info stored in the buffer by STB
        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();
        // Free
        stackPop();
        stackPop();

        // Find the correct openAL format
        // Init to a negative value, so it'll crash if it's not set to 1 or 2
        int format = -1;
        if (channels == 1) {
            format = AL_FORMAT_MONO16;
        } else if (channels == 2) {
            format = AL_FORMAT_STEREO16;
        }

        bufferId = alGenBuffers();
        alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

        // Generate the source
        sourceId = alGenSources();

        // These parameters can be changed at run time
        alSourcei(sourceId, AL_BUFFER, bufferId);
        alSourcei(sourceId, AL_LOOPING, loops ? 1:0);
        alSourcei(sourceId, AL_POSITION, 0);
        // Volume
        alSourcef(sourceId, AL_GAIN, 0.3f);

        // Free stb raw audio buffer
        free(rawAudioBuffer);
    }

    public void delete() {
        alDeleteSources(sourceId);
        alDeleteBuffers(bufferId);
    }

    public void play() {
        // Avoid playing multiple instances at once
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
            alSourcei(sourceId, AL_POSITION, 0);
        }
        if (!isPlaying) {
            alSourcePlay(sourceId);
            isPlaying = true;
        }
    }

    public void stop() {
        if (!isPlaying) {
            alSourceStop(sourceId);
            isPlaying = false;
        }
    }

    public String getFilepath() {
        return this.filepath;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(sourceId, AL_SOURCE_STATE);
        if (state == AL_STOPPED) {
            isPlaying = false;
        }
        return isPlaying;
    }
}
