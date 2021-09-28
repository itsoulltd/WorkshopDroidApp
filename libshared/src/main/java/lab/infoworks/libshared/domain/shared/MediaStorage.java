package lab.infoworks.libshared.domain.shared;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MediaStorage {

    public static class Builder implements MediaStoreFromUri, MediaStoreSelect, MediaStoreFetch, MediaStoreWhere, MediaStoreOrderBy{

        private Type type;
        private Class<?> mediaClsType;
        private Context appContext;

        public Builder(Context application) {
            this.appContext = (application instanceof Application)
                    ? application.getApplicationContext()
                    : application;
        }

        private Uri from;
        private String[] projection;
        private Predicate predicate;
        private Order order;
        private String orderColumn;

        @Override
        public MediaStoreSelect from(Type type) {
            this.type = type;
            if (type == Type.Video){
                mediaClsType = MediaStore.Video.class;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    from = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    from = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
            } else if (type == Type.Audio){
                mediaClsType = MediaStore.Audio.class;
            } else if (type == Type.Image){
                mediaClsType = MediaStore.Images.class;
            } else if (type == Type.File){
                mediaClsType = MediaStore.Files.class;
            } else if (type == Type.Download){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mediaClsType = MediaStore.Downloads.class;
                }
            }
            return this;
        }

        @Override
        public MediaStoreWhere select(String... projections) {
            this.projection = projections;
            return this;
        }

        @Override
        public MediaStoreOrderBy where(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        @Override
        public MediaStoreFetch orderBy(Order order, String column) {
            this.order = order;
            this.orderColumn = column;
            return this;
        }

        @Override
        public MediaStoreFetch orderBy(String column) {
            this.order = Order.ASC;
            this.orderColumn = column;
            return this;
        }

        @Override
        public <T extends MediaStoreItem> List<T> fetch(MediaStoreItemMapper<T> mapper) {
            AtomicInteger incrementer = new AtomicInteger(0);
            List<T> items = new ArrayList<>();
            try(Cursor cursor = appContext.getContentResolver().query(
                    from
                    , projection
                    , getSelection()
                    , getSelectionsArgs()
                    , getOrders())) {
                //...
                while (cursor.moveToNext()){
                    T item = mapper.map(cursor, incrementer.incrementAndGet());
                    if (item != null) items.add(item);
                }
            }
            return items;
        }

        private String[] getSelectionsArgs() {
            if (predicate != null){
                Expression[] expressions = predicate.resolveExpressions();
                if (expressions != null && expressions.length > 0){
                    List<String> exps = new ArrayList<>();
                    for (Expression exp : expressions) {
                        if (exp.getValueProperty().getValue() == null) continue;
                        exps.add(exp.getValueProperty().getValue().toString());
                    }
                    return exps.toArray(new String[0]);
                }
            }
            return new String[0];
        }

        private String getSelection() {
            if (predicate != null){
                Expression[] expressions = predicate.resolveExpressions();
                if (expressions != null && expressions.length > 0){
                    Expression expression = expressions[0];
                    String pro = expression.getProperty().replaceAll(" ", "");
                    pro = pro + " " + expression.getType().toString() + " ";
                    return pro;
                }
            }
            return null;
        }

        private String getOrders() {
            return orderColumn.replaceAll(" ", "") + " " + order.name();
        }

        private Bundle getQueryArgs(){
            return new Bundle();
        }
    }

    public enum Order {ASC, DESC}
    public enum Type {Video, Audio, Image, File, Download}

    public interface MediaStoreFromUri extends MediaStoreSelect{
        MediaStoreSelect from(Type type);
    }

    public interface MediaStoreSelect extends MediaStoreWhere{
        MediaStoreWhere select(String...projections);
    }

    public interface MediaStoreWhere extends MediaStoreOrderBy{
        MediaStoreOrderBy where(Predicate predicate);
    }

    public interface MediaStoreOrderBy extends MediaStoreFetch {
        MediaStoreFetch orderBy(Order order, String column);
        MediaStoreFetch orderBy(String column);
    }

    public interface MediaStoreFetch {
        <T extends MediaStoreItem> List<T> fetch(MediaStoreItemMapper<T> mapper);
    }

    public static class MediaStoreItem extends Entity {

        private final Uri uri;
        private final String name;
        private final int duration;
        private final int size;
        private final Type type;

        public MediaStoreItem(Type type, Uri uri, String name, int duration, int size) {
            this.type = type;
            this.uri = uri;
            this.name = name;
            this.duration = duration;
            this.size = size;
        }

        public Uri getUri() {
            return uri;
        }

        public String getName() {
            return name;
        }

        public int getDuration() {
            return duration;
        }

        public int getSize() {
            return size;
        }

        public Type getType() {
            return type;
        }
    }

    @FunctionalInterface
    public interface MediaStoreItemMapper<Mapper extends MediaStoreItem> {
        Mapper map(Cursor cursor, int index);
    }

}
