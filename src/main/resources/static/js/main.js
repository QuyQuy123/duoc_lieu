var token = localStorage.getItem("token");
const exceptionCode = 417;
$( document ).ready(function() {
    var auth = `<a href="/login" class="btn btn-light btn-custom-login">
            <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
          </a>
          <a href="/regis" class="btn btn-custom-register">
            <i class="bi bi-person"></i> Đăng ký
          </a>`
    if(token != null){
        auth = `
        <a href="#" onclick="logout()" class="btn btn-light btn-custom-login">
            <i class="bi bi-box-arrow-in-left"></i> Đăng xuất
          </a>
          <a href="/regis" class="btn btn-custom-register">
            <i class="bi bi-person"></i> Tài khoản
          </a>
        `
    }
    var headerUser = 
    `<nav class="navbar navbar-expand-lg bg-white py-3">
    <div class="container">
      <a class="navbar-brand d-flex align-items-center" href="/">
        <div class="logo-circle">DL</div>
        <span>DuocLieuVN</span>
      </a>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainMenu">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="mainMenu">
        <ul class="navbar-nav mx-auto mb-2 mb-lg-0 gap-lg-4">
          <li class="nav-item"><a class="nav-link active text-success fw-semibold border-bottom border-2 border-success" href="/index">Trang chủ</a></li>
          <li class="nav-item"><a class="nav-link" href="/plant">Cây dược liệu</a></li>
          <li class="nav-item"><a class="nav-link" href="/articles">Bài viết</a></li>
          <li class="nav-item"><a class="nav-link" href="/research">Nghiên cứu</a></li>
          <li class="nav-item"><a class="nav-link" href="/experts">Chuyên gia</a></li>
          <li class="nav-item"><a class="nav-link" href="/about">Giới thiệu</a></li>
        </ul>
        <div class="d-flex gap-2">
        ${auth}
        </div>
      </div>
    </div>
  </nav>`
  document.getElementById("header-main").innerHTML = headerUser

  var footerUser = 
  `<div class="container">
    <div class="row g-4">
      <div class="col-md-4">
        <div class="d-flex align-items-center mb-3">
          <div class="footer-logo">DL</div>
          <span class="fs-5 fw-bold">DuocLieuVN</span>
        </div>
        <p>Hệ thống quản lý thông tin cây dược liệu Việt Nam, cung cấp thông tin đầy đủ và chính xác.</p>
        <div class="d-flex gap-3">
          <a href="#"><i class="bi bi-facebook"></i></a>
          <a href="#"><i class="bi bi-twitter"></i></a>
          <a href="#"><i class="bi bi-instagram"></i></a>
        </div>
      </div>
      <div class="col-md-2">
        <h3 class="mb-3">Liên kết nhanh</h3>
        <ul class="list-unstyled">
          <li><a href="/">Trang chủ</a></li>
          <li><a href="/plants">Cây dược liệu</a></li>
          <li><a href="/articles">Bài viết</a></li>
          <li><a href="/research">Nghiên cứu</a></li>
          <li><a href="/experts">Chuyên gia</a></li>
          <li><a href="/about">Giới thiệu</a></li>
        </ul>
      </div>
      <div class="col-md-3">
        <h3 class="mb-3">Danh mục</h3>
        <ul class="list-unstyled">
          <li><a href="#">Thảo dược</a></li>
          <li><a href="#">Cây thuốc</a></li>
          <li><a href="#">Nấm dược liệu</a></li>
          <li><a href="#">Hoa dược liệu</a></li>
          <li><a href="#">Rễ dược liệu</a></li>
          <li><a href="#">Quả dược liệu</a></li>
        </ul>
      </div>
      <div class="col-md-3">
        <h3 class="mb-3">Liên hệ</h3>
        <ul class="list-unstyled">
          <li><i class="bi bi-geo-alt-fill me-2"></i> Số 123, Đường ABC, Hà Nội</li>
          <li><i class="bi bi-telephone-fill me-2"></i> +84 123 456 789</li>
          <li><i class="bi bi-envelope-fill me-2"></i> info@duoclieuvn.com</li>
        </ul>
      </div>
    </div>
    <div class="text-center border-top mt-4 pt-3">
      © 2025 DuocLieuVN. Tất cả các quyền được bảo lưu.
    </div>
  </div>
</div>
`

  document.getElementById("footer-main").innerHTML = footerUser

    function setActiveMenu() {
        // 1. Lấy đường dẫn URL hiện tại
        // Ví dụ: /plant-detail/xa-den -> sẽ lấy /plant-detail/xa-den
        // Ví dụ: /plant -> sẽ lấy /plant
        var currentPath = window.location.pathname;

        // 2. Tùy chỉnh: Nếu URL là chi tiết (vd: /plant-detail/...) thì ta muốn đánh dấu trang chủ đề (/plant) là active.
        // Lấy phần đầu tiên của URL (ví dụ: /plant-detail/xa-den -> /plant)
        var currentBaseUrl = currentPath.split('/')[1];

        // Nếu đường dẫn trống (trang chủ) thì dùng 'index'
        if (currentBaseUrl === '') {
            currentBaseUrl = 'index';
        }

        // 3. Xóa tất cả các class active hiện có
        $('#mainMenu .nav-link').removeClass('active text-success fw-semibold border-bottom border-2 border-success');

        // 4. Lặp qua tất cả các liên kết và kiểm tra
        $('#mainMenu .nav-link').each(function() {
            var linkHref = $(this).attr('href'); // Lấy href, ví dụ: /plant

            // Lấy phần href cơ bản (ví dụ: /plant -> plant, /index -> index)
            var linkBaseUrl = linkHref.split('/')[1] || 'index';

            // So sánh URL cơ bản của link với URL cơ bản hiện tại
            if (linkBaseUrl === currentBaseUrl) {
                $(this).addClass('active text-success fw-semibold border-bottom border-2 border-success');

                // Thoát khỏi vòng lặp sau khi đã tìm thấy liên kết phù hợp
                return false;
            }
        });
    }

    // Gọi hàm sau khi header đã được chèn vào DOM
    setActiveMenu();
});


function logout(){
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    window.location.href = '/logout'
}