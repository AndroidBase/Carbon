package pl.zielony.samples;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import pl.zielony.carbon.animation.TransitionLayout;

/**
 * Created by Marcin on 2015-01-24.
 */
public class RadialTransitionActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radial_transition);

        getSupportFragmentManager().beginTransaction().add(R.id.container,new SimpleFragment()).commit();

        final TransitionLayout transitionView = (TransitionLayout) findViewById(R.id.transition);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionView.setHotspot(v);
                transitionView.startTransition(Math.random()>0.5f? TransitionLayout.TransitionType.RadialExpand: TransitionLayout.TransitionType.RadialCollapse);
            }
        });
    }
}
