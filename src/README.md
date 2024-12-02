# Quản lý thư viện
*[UET](https://uet.vnu.edu.vn)- Assignment - Project*.

## Giới thiệu

### Nhóm và thành viên nhóm.

1 *Đinh Văn Quốc Chưởng - Mã Sinh Viên : 23020020*.

2 *Trịnh Bình Dương     - Mã Sinh Viên : 23020035*.

3 *Đoàn Đình Đăng       - Mã Sinh Viên : 23020041*.

### Lớp và Môn học:

Lớp : *K68 - IT2*.

Môn học : *INT2204 - 7 (Lập trình hướng đối tượng)*.
### Mô tả
Đây là một hệ thống quản lí thư viện sử dụng ngôn ngữ `java`. Hệ thống có thể quản lý sách, quản lý người dùng và có thể mượn trả sách có giao diện sử dụng `JavaFX` để cài đặt giao diện. Ứng dụng mang lại giải pháp quản lý sách đầy đủ và hoàn thiện. Dưới đây là các tính năng chính
#### Quản lý sách
#### Quản lý người dùng
#### Mượn trả sách
#### Thêm đánh giá sách
#### Tìm kiếm sách trên API
## Mục lục
1. [ Cách cài đặt thư viện.](#1-cách-cài-đặt-thư-viện)
2. [ Hướng dẫn sử dung.](#2-hướng-dẫn-sử-dụng)
3. [ Thư viện đã sử dụng.](#3-thư-viện-đã-sử-dụng)
4. [ Góp ý và đánh giá.](#4-góp--ý-và-đánh-giá)
5. [ Kết luận](#5-kết-luận)

### 1 Cách cài đặt thư viện.
#### 1.1 Dowload source code.
Dowload git và tạo tại khoản github đế sử dung. Sau đó dử dụng câu lệnh sau để dowload source code về :

```bash
git clone -b main https://github.com/chuongdinh277/QuanLiThuVien.git
```
#### 1.2 Dowload môi trường chạy JAVA và SQL

Môi trường JAVA : [IntelijIDEA](https://www.jetbrains.com/idea/download/?section=windows)
hoặc có thể sử dụng [Eclipse](https://www.eclipse.org/downloads/).

Cài đặt cơ sở dữ liệu : [MySQL](https://www.mysql.com/downloads)

Cài đặt hệ chạy MySQL : [MySQLWorkbench](https://dev.mysql.com/downloads/workbench/)

#### 1.3 Cài Đặt Java

Cài JDK : [JDK](https://www.oracle.com/java/technologies/downloads/)

#### 1.4 Framework

Sử dụng JavaFX để xây dựng thư viện có giao diện.
### 2. Hướng dẫn sử dụng.

#### 2.1 *Đăng ký và đăng nhập tài khoản.*
Khi bắt đầu bạn sẽ phải đăng ký một tài khoản để có thể vào chương trình hoặc có thể đăng nhập nếu bạn đã có tài khoản. Đây là màn hình giao diện đăng nhập của ứng dụng. ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20211112.png) Nếu bạn chưa có tài khoản thì bạn cần nhấn vào `Sing In` để tạo tài khoản đăng nhập ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20214905.png) để tạo tài khoản cho bạn để sử dụng thư viện. Sau khi tạo tài khoản bạn có thể dùng tài khoản để đăng nhập. Bạn cần nhập đúng tài khoản và mật khẩu và vai trò mà bạn đã đăng ký. Nếu bạn nhập sai mật khẩu hoặc tài khoản và cả vai trò thì bạn sẽ không thể đăng nhập vào được thư viện. Một tài khoản có thể có vai trò là admin hoặc user.
#### 2.2 *Giao diện của thư viện trong vai trò admin*
Đây là giao diện *Home admin* trong thư viện. ![](Image/login.jpg) Giao diện này sẽ hiển thị thời gian hiện tại và hiển thị các thành phần có trong thư viện. Khi bạn nhấn vào hình này trên màn hình ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20220246.png) bạn có hai lựa chọn là  `profile` và `logout`.

Nếu bạn chọn `profile` thì nó sẽ hiển thị ra profile của người dùng ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20140402.png)

Còn nếu bạn chọn `logout` thì sẽ chọn ra màn hình đăng nhập ban đầu.

Trong giao diện `home` thì nếu bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20222637.png) nó sẽ hiển thị ra danh sách các sách trong thư viện của bạn ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20222419.png). Nếu bạn nhấn vào `ADD BOOK` thì bạn sẽ vào trang để tìm kiếm sách trên dựa theo tên tác giả và tác giả để tìm sách [API google books](https://developers.google.com/books?hl=vi) ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20133714.png)

Còn nếu bạn nhấn vào quyền sách trên giá sách thì nó sẽ hiện ra chi tiết của quyển sách ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20223921.png) Đây là giao diện hiển thị chi tiết của quyển sách bạn chọn trong thư viện và bạn có thể `update` và `delete` sách nếu bạn có vai trò là một admin.

Khi bạn chọn `comment` thì nó sẽ hiện ra các commit của người dùng đánh giá cho sách và bạn cũng có thể tự đánh giá suy nghĩ của mình cho cuốn sách.![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20224126.png)

Còn nếu trong giao diện `home` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20222643.png) đây sẽ là logo để hiện ra dánh sách *students* có trong thư viện tương tự khi với sách khi nhấn vào sinh viên nó sẽ hiện ra thông tin cơ bản của sinh viên ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20134453.png)

Còn nếu trong giao diện `home` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20222641.png) nó sẽ hiện ra một trang để bạn cho mượn sách tới 1 sinh viên khi bạn có đầy đủ thông tin. Đây là giao diện của trang bạn sẽ cho sinh viên mượn sách ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20134442.png)

Còn nếu trong giao diện `home` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20222647.png) sẽ dẫn bạn đến trang tổng hợp sách đã mượn với thông tin đầy đủ: người mượn, ngày mượn, ngày trả. ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20134507.png)

Còn nếu trong giao diện `home` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20222651.png) thì bạn sẽ chuyển đến trang hiển thị thông tin đầy đủ của *student* theo `StudentID`. Đây là giao diện trang ấy ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20135029.png) Nó giúp bạn quan sát kĩ các sinh viên trong thư viện của bạn.

#### 2.3 *Giao diện thư viện trong vai trò user*

Nếu bạn là người dùng có vai trò User bạn sẽ được chuyển đến giao diện User.  Trong giao diện bạn có thể tìm kiếm sách theo tên dựa vào nut search thư viện sẽ trả ra danh sách các sách phù hợp với tên bạn tìm.

Đây là giao diện tìm kiếm dựa theo tên sách ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/z6085985622441_5308d0fc13f7fe1a262b2a4d9b0ec47e.jpg)

Khi bạn nhấn vào hình này trên màn hình ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20220246.png) bạn có hai lựa chọn là  `profile` và `logout`.

Nếu bạn chọn `profile` thì nó sẽ hiển thị ra profile của người dùng ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20133807.png)

Nếu trong giao diện `user` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20235357.png) bạn sẽ chuyển sang trang hiển thị sách trong thư viện.![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/z6085977309418_a7a54331db4620bff03bd53c9b9c7587.jpg)

Khi chọn một cuốn sách nó sẽ hiện ra thông tin sách chi tiết và nút `borrow` để bạn có thể mượn sách đó.![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20095306.png)

Nếu trong giao diện `user` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-11-30%20235737.png) bạn sẽ chuyển bạn sang trang mà chứa các sách mà bạn đang mượn của thư viện.![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/z6085978562130_1b443e7ebcf7318082e7cc93bbc8a877.jpg)

Tương tự với trên khi nhấn vào chi tiết thì sẽ có nút `return` để trả sách.![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20095532.png)

Nếu trong giao diện `user` bạn chọn nút ![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/Screenshot%202024-12-01%20135245.png) bạn sẽ chuyển về trang chủ với sách được gợi ý theo đánh giá và ngày thêm gần đây giúp bạn tìm kiếm được sách phù hợp để mượn và trong đó còn giúp tăng trải nghiệm người dùng. Đây là giao diện của  `homeUser`.![logo](https://github.com/dangdd2208/readmeIsReal/blob/main/z6085976051438_56d1a77c2e813ecc0e56f26e388cb4b2.jpg)

### 3. Thư viện đã sử dụng.

#### JavaFX :  JavaFX là một thư viện mạnh mẽ của Java dùng để phát triển giao diện người dùng (UI). Thư viện này giúp xây dựng các ứng dụng với giao diện đẹp mắt và dễ sử dụng. JavaFX hỗ trợ các tính năng như 2D/3D đồ họa, media, và webview, giúp cải thiện trải nghiệm người dùng.

#### Json : kết nốt dữ liệu với API Google books.

#### Mysql-connector : là một thư viện Java cung cấp giao diện để kết nối và tương tác với cơ sở dữ liệu MySQL. Thư viện này hỗ trợ các thao tác cơ bản như truy vấn, chèn, cập nhật và xóa dữ liệu từ cơ sở dữ liệu MySQL.

#### Junit && mock : : JUnit là một framework mã nguồn mở của Java dùng để kiểm thử phần mềm, đặc biệt là kiểm thử đơn vị (unit testing). JUnit giúp các nhà phát triển kiểm tra mã nguồn của họ để đảm bảo tính đúng đắn của các phương thức và chức năng trong ứng dụng. Việc sử dụng JUnit giúp đảm bảo rằng các chức năng của hệ thống hoạt động chính xác, đồng thời giảm thiểu lỗi khi phát triển ứng dụng.

### 4. Góp  ý và đánh giá.
Đây là phiên bản đầu tiên của thư viện. Mong nhận được nhiều ý kiến nhận xét của mọi người để có thể nâng cao chất lượng chương trình trong các phiên bản tiếp theo

Ý kiến đánh giá : Thông qua [Github Issues](https://github.com/chuongdinh277)

Gmail : 23020035@gmail.com.

Cảm ơn các ý kiến đánh giá và mong nhận được thêm các đánh giá.
### 5. Kết luận.
Hệ thống quản lý thư viện mang lại giải pháp toàn diện, áp dụng đầy đủ các kỹ thuật lập trình hướng đối tượng, kiểm thử JUnit, và tích hợp API. Đây là một dự án với tiềm năng mở rộng, phù hợp để ứng dụng thực tế hoặc làm nền tảng học tập.
- Sử dụng kế thừa trong lập trình hướng đối tượng để tạo ra các cây kế thừa giúp tối ưu code.
- Sử dụng *API google books* giúp tối ưu và chính xác kết quả tìm kiếm sách.
- Ngoài ra còn sử dụng đa luông(multi-thread) tối ưu các luồng và tăng trải nghiệm người dùng.
- Sử dụng JunitTest để tạo ra các test kiểm thử chương trình.
- Có sử dụng các design patterns trong code để tối ưu code hơn giảm độ phức tạp chương trình.
- Sử dụng đầy đủ các tính chất của lập trình hướng đối tượng vào code.
- Có hệ thống gợi ý sách cho người dùng tối hóa hóa trải nghiệm người dùng.

## Cảm ơn đã đọc.
