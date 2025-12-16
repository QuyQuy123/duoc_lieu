var size = 8;

async function loadAllArticle(page) {
  const param = document.getElementById("param").value || "";
  const diseases = document.getElementById("diseases").value || "";
  const sort = document.getElementById("sort").value
  var url = `http://localhost:8080/api/articles/public/all?page=${page}&size=${size}&q=${param}&sort=${sort}`;
  if(diseases != ""){
    url += `&diseasesId=${diseases}`
  }
  const response = await fetch(url, {
  });
  const result = await response.json();
  const list = result.content || [];
  const totalPage = result.totalPages || 0;

  let main = '';
  let mainGrid = '';
  for (let i = 0; i < list.length; i++) {
    const d = list[i];
    main += 
    `
    <div class="col-sm-6 col-lg-3">
        <div class="card h-100 shadow-sm">
        <div class="ratio ratio-16x9 bg-light d-flex align-items-center justify-content-center">
            <a href="/article-detail/${d.slug}"><img src="${d.imageBanner}" class="img-blog-list"></a>
        </div>
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-2">
            ${d.isFeatured==true?`<span class="badge bg-success">Nổi bật</span>`:''}
            <small class="text-muted"><i class="bi bi-calendar me-1"></i>${d.createdAt}</small>
            </div>
            <h5 class="card-title"><a class="tag-a" href="/article-detail/${d.slug}">${d.title}</a></h5>
            <p class="card-text text-muted">${d.excerpt}</p>
        </div>
        <div class="card-footer d-flex justify-content-between align-items-center">
            <small class="text-muted"><i class="bi bi-person me-1"></i>${d.user?.fullname}</small>
            <a href="/article-detail/${d.slug}" class="btn btn-outline-primary btn-sm">Đọc tiếp</a>
        </div>
        </div>
    </div>
    `;

    mainGrid += 
    `
    <div class="card mb-3 shadow-sm">
        <div class="row g-0">
            <div class="col-md-4 bg-light d-flex align-items-center justify-content-center" style="min-height:200px">
             <img src="${d.imageBanner}" style="width:100%">
            </div>
            <div class="col-md-8">
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="badge bg-primary">${d.diseases.name}</span>
                        <small class="text-muted"><i class="bi bi-calendar me-1"></i>${d.createdAt}</small>
                    </div>
                    <h5 class="card-title">${d.title}</h5>
                    <p class="card-text text-muted">${d.excerpt}</p>
                    <div class="d-flex justify-content-between">
                        <small class="text-muted">Tác giả: ${d.user?.fullname}</small>
                        <small class="text-muted">Lượt xem: ${d.views}</small>
                    </div>
                    <div class="text-end mt-3">
                        <a href="/article-detail/${d.slug}" class="btn btn-outline-primary btn-sm">Đọc tiếp</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `
  }
  document.getElementById("listData").innerHTML = main;
  document.getElementById("listDataGrid").innerHTML = mainGrid;


  renderPagination(page, totalPage)
}


async function loadDiseasesSelect() {
    const res = await fetch("http://localhost:8080/api/diseases/public/get-all-list");
    var list = await res.json();
    var main = '<option value="">Tất cả công dụng</option>';
    list.forEach(s => {
        main += `<option value="${s.id}">${s.name}</option>`;
    });
    document.getElementById("diseases").innerHTML = main
}

function renderPagination(currentPage, totalPages) {
    let maxVisiblePages = 3; // số trang hiển thị chính giữa
    let mainpage = '';

    // nút prev
    if (currentPage > 0) {
        mainpage += `<li class="page-item pointer" onclick="loadAllArticle(${currentPage - 1})"><a class="page-link">&laquo;</a></li>`;
    }

    // trang đầu
    if (currentPage > 1) {
        mainpage += `<li class="page-item pointer" onclick="loadAllArticle(0)"><a class="page-link">1</a></li>`;
    }

    // dấu ...
    if (currentPage > 2) {
        mainpage += `<li class="page-item disabled"><a class="page-link">...</a></li>`;
    }

    // trang ở giữa
    for (let i = currentPage - 1; i <= currentPage + 1; i++) {
        if (i >= 0 && i < totalPages) {
            mainpage += `<li onclick="loadAllArticle(${i})" class="page-item pointer ${i === currentPage ? 'active' : ''}"><a class="page-link">${i + 1}</a></li>`;
        }
    }

    // dấu ...
    if (currentPage < totalPages - 3) {
        mainpage += `<li class="page-item disabled"><a class="page-link">...</a></li>`;
    }

    // trang cuối
    if (currentPage < totalPages - 2) {
        mainpage += `<li class="page-item pointer" onclick="loadAllArticle(${totalPages - 1})"><a class="page-link">${totalPages}</a></li>`;
    }

    // nút next
    if (currentPage < totalPages - 1) {
        mainpage += `<li class="page-item pointer" onclick="loadAllArticle(${currentPage + 1})"><a class="page-link">&raquo;</a></li>`;
    }

    document.getElementById("pageable").innerHTML = mainpage;
}