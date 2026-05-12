(function () {
    "use strict";

    if (window.__disableDevtoolsLoaded) {
        return;
    }
    window.__disableDevtoolsLoaded = true;

    const blockedKeys = new Set(["F12"]);

    function showBlockedNotice() {
        let notice = document.getElementById("devtools-blocked-notice");
        if (!notice) {
            notice = document.createElement("div");
            notice.id = "devtools-blocked-notice";
            notice.setAttribute("role", "alert");
            notice.style.cssText = [
                "position:fixed",
                "top:18px",
                "right:18px",
                "z-index:99999",
                "max-width:320px",
                "padding:12px 16px",
                "border-radius:10px",
                "background:#064e3b",
                "color:#fff",
                "font:500 14px system-ui,sans-serif",
                "box-shadow:0 12px 30px rgba(0,0,0,.2)",
                "pointer-events:none",
                "opacity:0",
                "transform:translateY(-8px)",
                "transition:opacity .2s ease, transform .2s ease"
            ].join(";");
            notice.textContent = "Chức năng này đã bị vô hiệu hóa trên website.";
            document.body.appendChild(notice);
        }

        window.clearTimeout(notice.hideTimer);
        window.clearTimeout(notice.removeTimer);
        notice.style.opacity = "1";
        notice.style.transform = "translateY(0)";
        notice.hideTimer = window.setTimeout(function () {
            notice.style.opacity = "0";
            notice.style.transform = "translateY(-8px)";
            notice.removeTimer = window.setTimeout(function () {
                notice.remove();
            }, 250);
        }, 1800);
    }

    function blockEvent(event) {
        event.preventDefault();
        event.stopPropagation();
        showBlockedNotice();
        return false;
    }

    document.addEventListener("keydown", function (event) {
        const key = event.key || "";
        const normalizedKey = key.toUpperCase();
        const ctrlOrMeta = event.ctrlKey || event.metaKey;
        const isBlockedShortcut =
            blockedKeys.has(key) ||
            (ctrlOrMeta && event.shiftKey && ["I", "J", "C"].includes(normalizedKey)) ||
            (ctrlOrMeta && ["U", "S"].includes(normalizedKey));

        if (isBlockedShortcut) {
            blockEvent(event);
        }
    }, true);

    document.addEventListener("contextmenu", blockEvent, true);
})();
