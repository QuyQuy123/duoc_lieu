// --- Khai báo State và Data ---
let isOpen = false;
let messages = [];
let showSuggestions = false;
let selectedFile = null; // Biến lưu trữ file ảnh đã chọn/kéo

const suggestions = [
"Cây Sâm Ngọc Linh có công dụng gì?",
"Liệt kê các cây dược liệu dùng để chữa bệnh tiểu đường.",
"Tình trạng cây Quế tại lô S1-A hiện tại là gì?",
"Thời điểm thu hoạch tốt nhất của cây Xạ Đen là khi nào?",
"Tìm quy trình nhân giống cho cây Ba Kích.",
"Cây nào có hoạt chất tương tự cây Bạch Chỉ?",
"Nhiệt độ lý tưởng để trồng cây Lan Kim Tuyến là bao nhiêu?",
"Kiểm tra số lượng tồn kho của dược liệu Đinh Lăng.",
];

// --- Tham chiếu DOM ---
const chatbox = document.getElementById('chatbox');
const chatToggleBtn = document.getElementById('chat-toggle-btn');
const messagesContainer = document.getElementById('chat-messages');
const inputText = document.getElementById('chat-input-text');
const sendBtn = document.getElementById('send-message-btn');
const toggleSuggestionsBtn = document.getElementById('toggle-suggestions-btn');
const suggestionList = document.getElementById('suggestion-list');
const attachBtn = document.getElementById('attach-btn');
const imageFileInput = document.getElementById('image-file-input');
const dropZone = document.getElementById('drop-zone');
const imagePreviewArea = document.getElementById('image-preview-area'); // Mới
const previewImage = document.getElementById('preview-image'); // Mới
const removeImageBtn = document.getElementById('remove-image-btn'); // Mới


// Hàm mô phỏng API postMethodPayload
async function postMethodPayload(url, payload) {
return new Promise(resolve => {
setTimeout(() => {
let reply = "Đã nhận được yêu cầu của bạn. Vui lòng chờ phản hồi chi tiết.";
if (payload.hasImage) {
reply = "Cảm ơn bạn đã gửi ảnh! Tôi sẽ xem xét và phản hồi sớm.";
} else if (payload.message.includes('Menu') || payload.message.includes('món gì')) {
reply = "Menu hôm nay có Cá hồi áp chảo và Salad Nga.";
}
resolve({
json: () => Promise.resolve({ reply: reply })
});
}, 800);
});
}

// Hàm hiển thị/xóa preview ảnh
function updateImagePreview() {
if (selectedFile) {
// Hiển thị preview
const reader = new FileReader();
reader.onload = (e) => {
previewImage.src = e.target.result;
imagePreviewArea.classList.remove('hidden');
};
reader.readAsDataURL(selectedFile);
inputText.focus();
} else {
// Xóa preview
imagePreviewArea.classList.add('hidden');
previewImage.src = '';
inputText.placeholder = "Nhập tin nhắn...";
}
}

// Xử lý khi nhấn nút xóa ảnh
function removeImage() {
selectedFile = null;
imageFileInput.value = null; // Đặt lại input file
updateImagePreview();
}

// Hàm render lại danh sách tin nhắn (Đã cải tiến)
function renderMessages() {
messagesContainer.innerHTML = '';
messages.forEach((m, i) => {
const messageDiv = document.createElement('div');
messageDiv.className = `message ${m.role}`;

const contentSpan = document.createElement('span');
contentSpan.innerHTML = `<b>${m.role === "user" ? "Bạn" : "Bot"}:</b> ${m.text || (m.role === 'user' && m.file ? ' [Đã gửi ảnh]' : '')}`;
messageDiv.appendChild(contentSpan);

if (m.role === 'user' && m.file) {
const reader = new FileReader();
reader.onload = (e) => {
const img = document.createElement('img');
img.src = e.target.result;
img.className = 'message-image-preview';
messageDiv.appendChild(img);
messagesContainer.scrollTop = messagesContainer.scrollHeight;
};
reader.readAsDataURL(m.file);
}

messagesContainer.appendChild(messageDiv);
});
messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// Hàm render lại danh sách gợi ý (Giữ nguyên)
function renderSuggestions() {
suggestionList.innerHTML = '';
suggestions.forEach((s, idx) => {
const itemDiv = document.createElement('div');
itemDiv.className = 'suggestion-item';
itemDiv.textContent = s;
itemDiv.onclick = () => handleSuggestionClick(s);
suggestionList.appendChild(itemDiv);
});
}

// Hàm gửi tin nhắn (Đã cải tiến)
async function sendMessage() {
const message = inputText.value.trim();

if (!message && !selectedFile) return;

// 1. Cập nhật tin nhắn người dùng
const userMessage = {
role: "user",
text: message,
file: selectedFile
};
messages = [...messages, userMessage];

// Xóa nội dung input và file
inputText.value = "";
removeImage(); // Xóa preview và reset selectedFile

showSuggestions = false;
suggestionList.classList.add('hidden');
renderMessages();

// 2. Gọi API
const apiPayload = {
message: userMessage.text,
hasImage: !!userMessage.file
};

try {
const res = await postMethodPayload("/api/chat", apiPayload);
const data = await res.json();

// 3. Cập nhật tin nhắn bot
messages = [...messages, { role: "bot", text: data.reply }];
renderMessages();
} catch (error) {
messages = [...messages, { role: "bot", text: "Lỗi kết nối! Không thể nhận phản hồi." }];
renderMessages();
}
}

// Hàm xử lý khi click vào gợi ý
function handleSuggestionClick(suggestion) {
// Luôn luôn thêm gợi ý vào input text
inputText.value += (inputText.value ? " " : "") + suggestion;
inputText.focus();
}

// Hàm bật/tắt chat và gợi ý (Giữ nguyên)
function toggleChat() {
isOpen = !isOpen;
if (isOpen) {
chatbox.classList.remove('hidden');
chatToggleBtn.innerHTML = '✖';
showSuggestions = false;
suggestionList.classList.add('hidden');
} else {
chatbox.classList.add('hidden');
chatToggleBtn.innerHTML = '💬 Hỗ trợ AI';
}
}
function toggleSuggestions() {
showSuggestions = !showSuggestions;
suggestionList.classList.toggle('hidden', !showSuggestions);
}

// --- LOGIC XỬ LÝ FILE VÀ KÉO THẢ ---

// Xử lý khi có file ảnh được chọn/kéo
function handleFile(file) {
if (file.type.startsWith('image/')) {
selectedFile = file;
updateImagePreview();
} else {
alert("Chỉ hỗ trợ file ảnh.");
selectedFile = null;
}
}

// 1. Xử lý nút đính kèm
attachBtn.addEventListener('click', () => {
imageFileInput.click();
});

// 2. Xử lý khi chọn file bằng input
imageFileInput.addEventListener('change', (e) => {
if (e.target.files.length > 0) {
handleFile(e.target.files[0]);
e.target.value = null;
}
});

// 3. Xử lý nút xóa ảnh preview
removeImageBtn.addEventListener('click', removeImage);

// 4. Xử lý kéo và thả (Drag and Drop)
chatbox.addEventListener('dragenter', (e) => {
e.preventDefault();
if (e.dataTransfer.types && e.dataTransfer.types.includes('Files')) {
dropZone.classList.remove('hidden');
}
});
chatbox.addEventListener('dragleave', (e) => {
if (!chatbox.contains(e.relatedTarget)) {
dropZone.classList.add('hidden');
}
});
chatbox.addEventListener('dragover', (e) => {
e.preventDefault();
dropZone.classList.remove('hidden');
});
chatbox.addEventListener('drop', (e) => {
e.preventDefault();
dropZone.classList.add('hidden');

if (e.dataTransfer.files.length > 0) {
handleFile(e.dataTransfer.files[0]);
}
});


// --- Thiết lập Event Listeners khi DOM đã tải xong ---
document.addEventListener('DOMContentLoaded', () => {
// Khởi tạo trạng thái ban đầu
chatbox.classList.add('hidden');
suggestionList.classList.add('hidden');
imagePreviewArea.classList.add('hidden'); // Ẩn preview ban đầu
renderSuggestions();

// Gắn sự kiện
chatToggleBtn.addEventListener('click', toggleChat);
sendBtn.addEventListener('click', sendMessage);
toggleSuggestionsBtn.addEventListener('click', toggleSuggestions);

inputText.addEventListener('keydown', (e) => {
if (e.key === "Enter") {
sendMessage();
e.preventDefault();
}
});
});