package helper;

import myanotation.Column;
import myanotation.Entity;
import myanotation.Validate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class GenericValidateClass<T> {
    private Class<T> clazz; // chứa tất cả thông tin của generic type đang dùng.
    private HashMap<String, ArrayList<String>> errors = new HashMap<>();

    public GenericValidateClass(Class<T> clazz) {
        // kiểm tra class có được đánh dấu là @Entity hay không?
        if (!clazz.isAnnotationPresent(Entity.class)) {
            return;
        }
        this.clazz = clazz;
    }

    public boolean validate(T obj) {
        try {
            for (Field field : clazz.getDeclaredFields()) {
                // check xem trường có phải là @Column không.
                if (!field.isAnnotationPresent(Column.class)) {
                    // bỏ qua trong trường hợp không được đánh là @Column.
                    continue;
                }
                // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
                field.setAccessible(true);

                Column columnInfor = field.getAnnotation(Column.class);
                if (!field.isAnnotationPresent(Validate.class)) {
                    // bỏ qua trong trường hợp không được đánh là @Validate.
                    continue;
                }
                Validate validateInfo = field.getAnnotation(Validate.class);
                ArrayList<String> bugs = new ArrayList<String>();
                Object object = field.get(obj);
                if (validateInfo.required()) {
                    // Checking for null assignment
                    if (object == null) {
                        String str = String.format("Object : null / Column : %s", columnInfor.columnName());
                        bugs.add(str);

                        continue;
                    }

                    // Checking for String type
                    if (object instanceof String) {
                        String content = (String) object;
                        if (content.length() == 0) {
                            bugs.add(validateInfo.requiredMessage());
                        }
                    }
                }

                // Check co phai email hay khon
                if (validateInfo.isEmail()) {
                    if (object instanceof String) {
                        String emailStr = (String) object;
                        if (!RegexHelper.validate(emailStr)){
                            String str = String.format("Email must is this formatted \n Example : skyfury2651@gmail.com","");
                            bugs.add(str);
                        }
                    }
                }

                if (bugs.size() != 0){
                    this.errors.put(columnInfor.columnName(),bugs);
                }
            }

            return errors.size() == 0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    public HashMap<String, ArrayList<String>> getErrors() {
        return this.errors;
    }
}
