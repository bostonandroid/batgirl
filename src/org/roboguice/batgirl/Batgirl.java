package org.roboguice.batgirl;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.Random;


/**
 * An Android activity that uses injection to simplify its onCreate
 */
public class Batgirl extends RoboActivity {
    // Views
    @InjectView(R.id.content) LinearLayout linearLayout;

    // Resources
    @InjectResource(R.anim.spin) Animation spin;
    @InjectResource(R.integer.max_punches) int MAX_PUNCHES;

    // Other Injections
    @Inject ChangeTextAnimationListener changeTextAnimationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        linearLayout.setAnimation(spin);
        spin.setAnimationListener(changeTextAnimationListener);
        spin.setRepeatCount(MAX_PUNCHES - 1);

        spin.start();
    }
}

/**
 * A simple animation listener that swaps out the text between repeats.
 * 
 * When this class is injected into Batgirl above, its own @Injection
 * annotations will be processed, eg. helloView and fist will both be
 * injected.
 *
 * Since we haven't customized it, first will be injected with an
 * instance created using the default constructor for Fist.
 */
class ChangeTextAnimationListener implements AnimationListener {
    @InjectView(R.id.hello) TextView helloView;
    @Inject Fist fist;
    @Inject PackageInfo info;

    public void onAnimationRepeat(Animation animation) {
        onAnimationStart(animation);
    }

    public void onAnimationStart(Animation animation) {
        helloView.setText( fist.punch() );
    }

    public void onAnimationEnd(Animation animation) {
        helloView.setText( info.versionName ); // bonus surprise from AndroidManifest
    }
}


/**
 * Batgirls Fists, baby. They know how to punch.
 *
 * Again, "random" and "context" will be injected at the time this is
 * injected into ChangeTextAnimationListener.
 *
 * And again, the default constructor will be used for random.
 *
 * However, RoboGuice ships with a custom binding for Context,
 * so context will be injected with the current context (aka the
 * Batgirl activity instance in this case)
 */
class Fist {
    @Inject Random random;
    @Inject Context context;

    int[] array= new int[]{ R.string.pow, R.string.blammo };

    /** @return a random onomatopoeia from strings.xml for every call */
    public String punch() {
        return context.getString(array[random.nextInt(array.length)]);
    }

}

