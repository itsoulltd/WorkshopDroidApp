package lab.infoworks.starter.ui.activities.app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import lab.infoworks.starter.R;

public class NavStack {

    public static NavStack create(FragmentManager manager){
        return new NavStack(manager);
    }

    private FragmentManager manager;

    private NavStack(FragmentManager manager) {
        this.manager = manager;
    }

    public FragmentManager getSupportFragmentManager() {
        return manager;
    }

    private List<Fragment> navStack = new ArrayList<>();
    private List<String> tagStack = new ArrayList<>();

    public void pushNavStack(Fragment fragment, String tag) {
        navStack.add(0, fragment);
        if (tag != null && !tag.isEmpty()){
            tagStack.add(0, tag);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment, tag)
                    .commit();
        }
    }


    public void popNavStack(){
        if (navStack.size() <= 1) return;
        Fragment fragment = navStack.remove(0);
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
        //pop-next and push to fragment-maager
        fragment = navStack.remove(0);
        String tag = tagStack.remove(0);
        pushNavStack(fragment, tag);
    }

    public void close() {
        manager = null;
        navStack.clear();
        tagStack.clear();
    }
}
