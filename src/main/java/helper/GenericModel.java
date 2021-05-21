package helper;

import entity.SqlCondition;
import myanotation.Column;
import myanotation.Entity;
import myanotation.Id;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenericModel<T> {
    private Class<T> clazz;
    public GenericModel(Class<T> clazz){
        if (!clazz.isAnnotationPresent(Entity.class)){
            System.err.printf("Class %s không được đăng ký làm việc với database.",clazz.getSimpleName());
            return;
        }
        this.clazz = clazz;
    }
    // lưu thông tin đối tượng kiểu T
    public boolean save(T obj) {
        try {
            // Lấy ra giá trị của annotation @Entity vì cần những thông tin liên quan đến tableName.
            Entity entityInfor = clazz.getAnnotation(Entity.class);
            // Build lên câu query string.
            StringBuilder strQuery = new StringBuilder();
            // Build chuỗi chứa giá trị các trường tương ứng.
            StringBuilder fieldValues = new StringBuilder();
            fieldValues.append(SQLConstant.OPEN_PARENTHESES);
            // Xây dựng câu lệnh insert theo tên bảng, theo tên các field cùa đối tượng truyền vào.
            strQuery.append(SQLConstant.INSERT_INTO); // insert into
            strQuery.append(SQLConstant.SPACE); //
            strQuery.append(entityInfor.tableName()); // giangvien
            strQuery.append(SQLConstant.SPACE); //
            strQuery.append(SQLConstant.OPEN_PARENTHESES); // (
            for (Field field : clazz.getDeclaredFields()) {
                // check xem trường có phải là @Column không.
                if (!field.isAnnotationPresent(Column.class)) {
                    // bỏ qua trong trường hợp không được đánh là @Column.
                    continue;
                }
                // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
                field.setAccessible(true);
                // lấy thông tin column để check tên trường, kiểu giá trị của trường.
                // Không lấy danh sách column theo tên field mà lấy theo annotation đặt tại field đó.
                Column columnInfor = field.getAnnotation(Column.class);
                // check xem trường có phải là id không.
                if (field.isAnnotationPresent(Id.class)) {
                    // lấy thông tin id.
                    Id idInfor = field.getAnnotation(Id.class);
                    if (idInfor.autoIncreament()) {
                        // trường hợp đây là trường tự tăng, thì next sang trường tiếp theo.
                        continue;
                    }
                }
                strQuery.append(columnInfor.columnName()); // nối tên trường.
                strQuery.append(SQLConstant.COMMON); //,
                strQuery.append(SQLConstant.SPACE); //
                // nhanh trí, xử lý luôn phần value, tránh sử dụng 2 vòng lặp.
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName()) || field.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                // lấy ra thông tin giá trị của trường đó tại obj truyền vào.
                fieldValues.append(field.get(obj)); // field.setAccessible(true);
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())  || field.getType().getSimpleName().equals(Date.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                fieldValues.append(SQLConstant.COMMON); //,
                fieldValues.append(SQLConstant.SPACE); //
            }
            strQuery.setLength(strQuery.length() - 2); // trường hợp là field cuối cùng thì bỏ dấu , và khoảng trắng đi.
            fieldValues.setLength(fieldValues.length() - 2);
            strQuery.append(SQLConstant.CLOSE_PARENTHESES); // )
            fieldValues.append(SQLConstant.CLOSE_PARENTHESES); // )
            strQuery.append(SQLConstant.SPACE);
            strQuery.append(SQLConstant.VALUES); // values
            strQuery.append(SQLConstant.SPACE);
            strQuery.append(fieldValues); // nối giá trị các trường vào.
            System.out.println("lệnh insert \n");
            System.out.println(strQuery.toString());
            return ConnectionHelper.getConnection().createStatement().execute(strQuery.toString());
        } catch (IllegalAccessException | SQLException e) {
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        return true;
    }

    public List<T> findAll() {
        List<T> result = new ArrayList<>(); // khởi tạo một danh sách rỗng.
        Entity entityInfor = clazz.getAnnotation(Entity.class);
        StringBuilder stringQuery = new StringBuilder();
        stringQuery.append(SQLConstant.SELECT_ASTERISK); // select *
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.FROM); // from
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(entityInfor.tableName()); // tableName
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringQuery.toString());
            // thực thi câu lệnh select * from.
            // trả về ResultSet (nó thêm thằng con trỏ)
            ResultSet resultSet = preparedStatement.executeQuery();
            Field[] fields = clazz.getDeclaredFields(); //
            while (resultSet.next()) { // trỏ đến các bản ghi cho đến khi trả về false.
                T obj = clazz.newInstance(); // khởi tạo ra đối tượng cụ thể của class T.
                for (Field field : fields) {
                    // check nếu không là @Column
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    // lấy thông tin column để check tên trường, kiểu giá trị của trường.
                    Column columnInfor = field.getAnnotation(Column.class);
                    // tuỳ thuộc vào kiểu dữ liệu của trường, lấy giá trị ra theo các hàm khác nhau.
                    // phải bổ sung các kiểu dữ liệu cần thiết.
                    switch (field.getType().getSimpleName()) {
                        case SQLConstant.PRIMITIVE_INT:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_STRING:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                    }
                }
                // đối tượng obj kiểu T đã có đầy đủ giá trị.
                // add vào trong danh sách trả về.
                result.add(obj);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        return result;
    }

    public List<T> findAll(int page , int perPage) {
        List<T> result = new ArrayList<>(); // khởi tạo một danh sách rỗng.
        Entity entityInfor = clazz.getAnnotation(Entity.class);
        StringBuilder stringQuery = new StringBuilder();
        stringQuery.append(SQLConstant.SELECT_ASTERISK); // select *
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.FROM); // from
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(entityInfor.tableName()); // tableName
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(SQLConstant.LIMIT);
        stringQuery.append(SQLConstant.SPACE);
        stringQuery.append(page * perPage);
        stringQuery.append(SQLConstant.COMMON);
        stringQuery.append(perPage);
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringQuery.toString());
            // thực thi câu lệnh select * from.
            // trả về ResultSet (nó thêm thằng con trỏ)
            ResultSet resultSet = preparedStatement.executeQuery();
            Field[] fields = clazz.getDeclaredFields(); //
            while (resultSet.next()) { // trỏ đến các bản ghi cho đến khi trả về false.
                T obj = clazz.newInstance(); // khởi tạo ra đối tượng cụ thể của class T.
                for (Field field : fields) {
                    // check nếu không là @Column
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    // lấy thông tin column để check tên trường, kiểu giá trị của trường.
                    Column columnInfor = field.getAnnotation(Column.class);
                    // tuỳ thuộc vào kiểu dữ liệu của trường, lấy giá trị ra theo các hàm khác nhau.
                    // phải bổ sung các kiểu dữ liệu cần thiết.
                    switch (field.getType().getSimpleName()) {
                        case SQLConstant.PRIMITIVE_INT:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_STRING:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                    }
                }
                // đối tượng obj kiểu T đã có đầy đủ giá trị.
                // add vào trong danh sách trả về.
                result.add(obj);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        return result;
    }

    public T findById(int id) {
        Entity entity = clazz.getAnnotation(Entity.class);
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();
        stringBuilder.append(SQLConstant.SELECT_ASTERISK);
        stringBuilder.append(SQLConstant.SPACE);
        stringBuilder.append(SQLConstant.FROM);
        stringBuilder.append(SQLConstant.SPACE);
        stringBuilder.append(entity.tableName());
        stringBuilder.append(SQLConstant.SPACE);
        stringBuilder.append(SQLConstant.WHERE);
        stringBuilder.append(SQLConstant.SPACE);
        for (Field field : clazz.getDeclaredFields()) {
            // check xem trường có phải là @Column không.
            if (!field.isAnnotationPresent(Column.class)) {
                // bỏ qua trong trường hợp không được đánh là @Column.
                continue;
            }
            // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
            field.setAccessible(true);

            Column columnInfor = field.getAnnotation(Column.class);
            if (field.isAnnotationPresent(Id.class)) {
                // lấy thông tin id.
                stringBuilder.append(columnInfor.columnName()); // nối tên trường.
                stringBuilder.append(SQLConstant.SPACE); //
                stringBuilder.append(SQLConstant.EQUAL); //
                stringBuilder.append(SQLConstant.SPACE); //
                // nhanh trí, xử lý luôn phần value, tránh sử dụng 2 vòng lặp.
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                // lấy ra thông tin giá trị của trường đó tại obj truyền vào.
                fieldValues.append(id); // field.setAccessible(true);
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                fieldValues.append(SQLConstant.SPACE); //
                stringBuilder.append(fieldValues); //
            }
        }
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringBuilder.toString());
            // thực thi câu lệnh select * from.
            // trả về ResultSet (nó thêm thằng con trỏ)
            ResultSet resultSet = preparedStatement.executeQuery();
            Field[] fields = clazz.getDeclaredFields(); //
            while (resultSet.next()) { // trỏ đến các bản ghi cho đến khi trả về false.
                T obj = clazz.newInstance(); // khởi tạo ra đối tượng cụ thể của class T.
                for (Field field : fields) {
                    // check nếu không là @Column
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    // lấy thông tin column để check tên trường, kiểu giá trị của trường.
                    Column columnInfor = field.getAnnotation(Column.class);
                    // tuỳ thuộc vào kiểu dữ liệu của trường, lấy giá trị ra theo các hàm khác nhau.
                    // phải bổ sung các kiểu dữ liệu cần thiết.
                    switch (field.getType().getSimpleName()) {
                        case SQLConstant.PRIMITIVE_INT:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_STRING:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                    }
                }
                // đối tượng obj kiểu T đã có đầy đủ giá trị.
                // add vào trong danh sách trả về.

                return obj;
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        System.out.println(stringBuilder.toString());
        return null;
    }

    public T update(int id,T obj) throws InstantiationException, IllegalAccessException, SQLException {
        // Lấy ra giá trị của annotation @Entity vì cần những thông tin liên quan đến tableName.
        Entity entityInfor = clazz.getAnnotation(Entity.class);
        // Build lên câu query string.
        StringBuilder strQuery = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        // Xây dựng câu lệnh update theo tên bảng, theo tên các field cùa đối tượng truyền vào.
        strQuery.append(SQLConstant.UPDATE); // insert into
        strQuery.append(SQLConstant.SPACE); //
        strQuery.append(entityInfor.tableName()); // giangvien
        strQuery.append(SQLConstant.SPACE); //
        strQuery.append(SQLConstant.SET); //
        strQuery.append(SQLConstant.SPACE); //
        for (Field field : clazz.getDeclaredFields()) {
            // check xem trường có phải là @Column không.
            if (!field.isAnnotationPresent(Column.class)) {
                // bỏ qua trong trường hợp không được đánh là @Column.
                continue;
            }
            // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
            field.setAccessible(true);
            // lấy thông tin column để check tên trường, kiểu giá trị của trường.
            // Không lấy danh sách column theo tên field mà lấy theo annotation đặt tại field đó.
            Column columnInfor = field.getAnnotation(Column.class);
            // check xem trường có phải là id không.
            if (!field.isAnnotationPresent(Id.class)) {
                strQuery.append(columnInfor.columnName()); // nối tên trường.
                strQuery.append(SQLConstant.SPACE); //
                strQuery.append(SQLConstant.EQUAL); //,
                strQuery.append(SQLConstant.SPACE); //
                // nhanh trí, xử lý luôn phần value, tránh sử dụng 2 vòng lặp.
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    strQuery.append(SQLConstant.QUOTE);
                }
                strQuery.append(field.get(obj));
//                    fieldValues.append(field.get(obj)); // field.setAccessible(true);
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    strQuery.append(SQLConstant.QUOTE);
                }
                strQuery.append(SQLConstant.COMMON); // nối giá trị các trường vào.
                strQuery.append(SQLConstant.SPACE); // nối giá trị các trường vào.
            }
        }
        strQuery.setLength(strQuery.length() - 2);
        strQuery.append(SQLConstant.SPACE);
        strQuery.append(SQLConstant.WHERE); // nối giá trị các trường vào.
        strQuery.append(SQLConstant.SPACE);
        for (Field field1 : clazz.getDeclaredFields()) {
            // check xem trường có phải là @Column không.
            if (!field1.isAnnotationPresent(Column.class)) {
                // bỏ qua trong trường hợp không được đánh là @Column.
                continue;
            }
            // cần set bằng true để có thể set, get giá trị của field trong một object nào đó.
            field1.setAccessible(true);

            Column columnInfor = field1.getAnnotation(Column.class);
            if (field1.isAnnotationPresent(Id.class)) {
                // lấy thông tin id.
                strQuery.append(columnInfor.columnName()); // nối tên trường.
                strQuery.append(SQLConstant.SPACE); //
                strQuery.append(SQLConstant.EQUAL); //
                strQuery.append(SQLConstant.SPACE); //
                // nhanh trí, xử lý luôn phần value, tránh sử dụng 2 vòng lặp.
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field1.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                // lấy ra thông tin giá trị của trường đó tại obj truyền vào.
                fieldValues.append(id); // field.setAccessible(true);
                // check kiểu của trường, nếu là string thì thêm dấu '
                if (field1.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    fieldValues.append(SQLConstant.QUOTE);
                }
                fieldValues.append(SQLConstant.SPACE); //
                strQuery.append(fieldValues); //
            }
        }
        System.out.println("lệnh update \n");
        System.out.println(strQuery.toString());
        ConnectionHelper.getConnection().createStatement().execute(strQuery.toString());
        return obj;
    }

    public boolean delete(int id) throws SQLException {
        try{
            Entity entity = clazz.getAnnotation(Entity.class);
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder fieldValues = new StringBuilder();
            stringBuilder.append(SQLConstant.DELETE);
            stringBuilder.append(SQLConstant.SPACE);
            stringBuilder.append(SQLConstant.FROM);
            stringBuilder.append(SQLConstant.SPACE);
            stringBuilder.append(entity.tableName());
            stringBuilder.append(SQLConstant.SPACE);
            stringBuilder.append(SQLConstant.WHERE);
            stringBuilder.append(SQLConstant.SPACE);
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Column.class)) {
                    continue;
                }
                field.setAccessible(true);

                Column columnInfor = field.getAnnotation(Column.class);
                if (field.isAnnotationPresent(Id.class)) {
                    stringBuilder.append(columnInfor.columnName());
                    stringBuilder.append(SQLConstant.SPACE);
                    stringBuilder.append(SQLConstant.EQUAL);
                    stringBuilder.append(SQLConstant.SPACE);
                    if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                        fieldValues.append(SQLConstant.QUOTE);
                    }
                    fieldValues.append(id);
                    if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                        fieldValues.append(SQLConstant.QUOTE);
                    }
                    fieldValues.append(SQLConstant.SPACE);
                    stringBuilder.append(fieldValues);
                }
            }
            System.out.println(stringBuilder.toString());
            return ConnectionHelper.getConnection().createStatement().execute(stringBuilder.toString());

        }catch (SQLException e){
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        return false;
    }

    public T findByColumns(HashMap<String, SqlCondition> hashMap){
        Entity entity = clazz.getAnnotation(Entity.class);
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();
        stringBuilder.append(SQLConstant.SELECT_ASTERISK);
        stringBuilder.append(SQLConstant.SPACE);
        stringBuilder.append(SQLConstant.FROM);
        stringBuilder.append(SQLConstant.SPACE);
        stringBuilder.append(entity.tableName());
        stringBuilder.append(SQLConstant.SPACE);
        stringBuilder.append(SQLConstant.WHERE);
        stringBuilder.append(SQLConstant.SPACE);
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            field.setAccessible(true);

            Column columnInfor = field.getAnnotation(Column.class);
            if (hashMap.containsKey(columnInfor.columnName())){
                stringBuilder.append(columnInfor.columnName());
                stringBuilder.append(SQLConstant.SPACE);
                stringBuilder.append(hashMap.get(columnInfor.columnName()).getExpression());
                stringBuilder.append(SQLConstant.SPACE);
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    stringBuilder.append(SQLConstant.QUOTE);
                }
                stringBuilder.append(hashMap.get(columnInfor.columnName()).getValue());
                if (field.getType().getSimpleName().equals(String.class.getSimpleName())) {
                    stringBuilder.append(SQLConstant.QUOTE);
                }
                hashMap.remove(columnInfor.columnName());
                if (hashMap.size() != 0){
                    stringBuilder.append(SQLConstant.SPACE);
                    stringBuilder.append(SQLConstant.AND);
                    stringBuilder.append(SQLConstant.SPACE);
                }
            }
        }
        try {
            PreparedStatement preparedStatement = ConnectionHelper.getConnection().prepareStatement(stringBuilder.toString());
            // thực thi câu lệnh select * from.
            // trả về ResultSet (nó thêm thằng con trỏ)
            ResultSet resultSet = preparedStatement.executeQuery();
            Field[] fields = clazz.getDeclaredFields(); //
            while (resultSet.next()) { // trỏ đến các bản ghi cho đến khi trả về false.
                T obj = clazz.newInstance(); // khởi tạo ra đối tượng cụ thể của class T.
                for (Field field : fields) {
                    // check nếu không là @Column
                    if (!field.isAnnotationPresent(Column.class)) {
                        continue;
                    }
                    field.setAccessible(true);
                    // lấy thông tin column để check tên trường, kiểu giá trị của trường.
                    Column columnInfor = field.getAnnotation(Column.class);
                    // tuỳ thuộc vào kiểu dữ liệu của trường, lấy giá trị ra theo các hàm khác nhau.
                    // phải bổ sung các kiểu dữ liệu cần thiết.
                    switch (field.getType().getSimpleName()) {
                        case SQLConstant.PRIMITIVE_INT:
                            // set giá trị của trường đó cho đối tượng mới tạo ở trên.
                            field.set(obj, resultSet.getInt(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_STRING:
                            field.set(obj, resultSet.getString(columnInfor.columnName()));
                            break;
                        case SQLConstant.PRIMITIVE_DOUBLE:
                            field.set(obj, resultSet.getDouble(columnInfor.columnName()));
                            break;
                    }
                }
                // đối tượng obj kiểu T đã có đầy đủ giá trị.
                // add vào trong danh sách trả về.

                return obj;
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            System.err.printf("Có lỗi xảy ra trong quá trình làm việc với database. Error %s.\n", e.getMessage());
        }
        System.out.println(stringBuilder.toString());
        return null;
    }
}
