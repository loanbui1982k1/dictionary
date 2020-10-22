package sample;

import com.sun.speech.freetts.VoiceManager;

public class Voice {
    private String name;

    private com.sun.speech.freetts.Voice voice;

    /*
    public Voice(String name) {
        this.name = name;
        this.voice = VoiceManager.getInstance().getVoice(this.name);
        this.voice.allocate();
    }

     */

    public Voice() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        voice = VoiceManager.getInstance().getVoice("kevin16");
        if (voice != null) {
            voice.allocate();// Allocating Voice
            try {
                voice.setRate(190);// Setting the rate of the voice
                voice.setPitch(150);// Setting the Pitch of the voice
                voice.setVolume(3);// Setting the volume of the voice
                //this.voice.speak(words);// Calling speak() method

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else {
            throw new IllegalStateException("Cannot find voice: kevin16");
        }
    }

    public void say(String something) {
        this.voice.speak(something);
    }

    public void sayMultiple(String sayMePls) {
            this.say(sayMePls);
    }
}
