var size = 10;
async function loadAllUser(page) {
    var param = document.getElementById("param").value
    var url = 'http://localhost:8080/api/admin/get-user-by-role?page=' + page + '&size=' + size + '&q=' + param;
    var role = document.getElementById("role").value
    if (role != "") {
        url += '&role=' + role
    }
    const response = await fetch(url, {
        method: 'GET',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var result = await response.json();
    console.log(result)
    var listUser = result.content;
    var totalPage = result.totalPages;
    const totalElements = result.totalElements || 0;
    const numberOfElements = result.numberOfElements || 0;
    const start = totalElements === 0 ? 0 : page * size + 1;
    const end = totalElements === 0 ? 0 : page * size + numberOfElements;   

    var main = '';
    for (i = 0; i < listUser.length; i++) {
        main += `<tr>
                    <td>${listUser[i].id}</td>
                    <td>${listUser[i].fullname}</td>
                    <td>${listUser[i].username} ${listUser[i].userType == 'EMAIL'?'':'<img src="/image/google.png" class="img-icon">'}</td>
                    <td>${listUser[i].email}</td>
                    <td>${listUser[i].authorities.name}</td>
                    <td><span class="badge p-2 ${listUser[i].actived == true?'badge-active':'badge-inactive'}">
                    ${listUser[i].actived == true ?'Hoạt động':'Đang khóa'}
                    </span></td>
                    <td>${listUser[i].createdDate}</td>
                    <td class="text-center">
                        <a href="/admin/create-user?id=${listUser[i].id}" class="btn btn-primary btn-sm" title="Sửa"><i class="fa-solid fa-pencil"></i></a>
                        <button onclick="deleteAccount(${listUser[i].id})" class="btn btn-danger btn-sm " title="Xóa"><i class="fa-solid fa-xmark"></i></button>
                    </td>
                </tr>`
    }
    document.getElementById("listuser").innerHTML = main
    var mainpage = ''
    for (i = 1; i <= totalPage; i++) {
        mainpage += `<li onclick="loadAllUser(${(Number(i) - 1)})" class="page-item"><a class="page-link" href="#listsp">${i}</a></li>`
    }
    document.getElementById("pageable").innerHTML = mainpage
    document.getElementById("numElm").innerText = `Đang hiển thị ${start} - ${end} trong ${result.totalElements} kết quả`
}


async function loadAuthority() {
    var url = 'http://localhost:8080/api/admin/authority';
    const response = await fetch(url, {
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    var list = await response.json();

    var main = '';
    for (i = 0; i < list.length; i++) {
        main += `<option value="${list[i].name}">${list[i].name}</option>`
    }
    document.getElementById("role").innerHTML = main
}


async function addAccount() {
    if(document.getElementById("password").value != document.getElementById("confirm-password").value){
        toastr.error("Mật khẩu không trùng khớp"); return;
    }
    var uls = new URL(document.URL)
    var id = uls.searchParams.get("id");
    if(document.getElementById("password").value == '' && id == null){
        toastr.error("Mật khẩu không được để trống"); return;
    }
    var user = {
        "id":id,
        "fullname": document.getElementById("fullname").value,
        "phone": document.getElementById("phone").value,
        "email": document.getElementById("email").value,
        "password": document.getElementById("password").value,
        "username": document.getElementById("username").value,
        "address": document.getElementById("address").value,
        "actived": document.getElementById("actived").checked,
        "authorities": {
            "name":document.getElementById("role").value
        },
    }
    const res = await fetch('http://localhost:8080/api/admin/addaccount', {
        method: 'POST',
        headers: new Headers({
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(user)
    });
    var result = await res.json();
    if (res.status < 300) {
        swal({
                title: "Thông báo",
                text: "Tạo tài khoản thành công!",
                type: "success"
            },
            function() {
                window.location.href = '/admin/account/list';
            });
    }
    if (res.status == exceptionCode) {
        swal({
            title: "Thông báo",
            text: result.defaultMessage,
            type: "error"
        }, function() {
        });
    }
}

async function loadAUser() {
    var id = window.location.search.split('=')[1];
    if (id != null) {
        document.getElementById("note-pass").style.display = 'block';
        const response = await fetch('http://localhost:8080/api/admin/find-user-by-id?id=' + id, {
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });
        var user = await response.json();
        document.getElementById("username").value = user.username
        document.getElementById("email").value = user.email
        document.getElementById("address").value = user.address
        document.getElementById("phone").value = user.phone
        document.getElementById("fullname").value = user.fullname
        document.getElementById("role").value = user.authorities.name
        document.getElementById("actived").checked = user.actived
    }
}



async function deleteAccount(id) {
    var con = confirm("Xác nhận xóa tài khoản này?")
    if (con == false) {
        return;
    }
    var url = 'http://localhost:8080/api/admin/delete-user-by-id?id=' + id;
    const response = await fetch(url, {
        method: 'DELETE',
        headers: new Headers({
            'Authorization': 'Bearer ' + token
        })
    });
    if (response.status < 300) {
        swal({
            title: "Thông báo",
            text: "xóa tài khoản thành công!",
            type: "success"
        },
        function() {
            window.location.reload();
        });
    }
    if (response.status == exceptionCode) {
        toastr.warning(result.defaultMessage);
        swal({
            title: "Thông báo",
            text: result.defaultMessage,
            type: "error"
        },
        function() {
        });
    }
}

/**
 * Xuất danh sách người dùng ra CSV, áp dụng bộ lọc hiện tại.
 */
async function exportUsers() {
    const param = document.getElementById("param").value || "";
    const role = document.getElementById("role").value || "";

    let url = `http://localhost:8080/api/admin/export-users?q=${encodeURIComponent(param)}`;
    if (role !== "") {
        url += `&role=${encodeURIComponent(role)}`;
    }

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: new Headers({
                'Authorization': 'Bearer ' + token
            })
        });

        if (!response.ok) {
            toastr.error("Lỗi khi xuất Excel");
            return;
        }

        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = 'users_export.csv';
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(downloadUrl);
    } catch (e) {
        console.error(e);
        toastr.error("Có lỗi xảy ra khi xuất Excel");
    }
}