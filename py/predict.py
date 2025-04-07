from flask import Flask, request, jsonify, send_file
from flask_cors import CORS
from ultralytics import YOLO
import cv2
import numpy as np
import io
import time
import os
import configparser

app = Flask(__name__)
CORS(app)

# 配置文件路径
CONFIG_FILE = "model_config.properties"

# 读取配置文件获取当前模型名称
def get_current_model_name():
    default_model = "best.pt"
    
    # 如果配置文件不存在，使用默认模型
    if not os.path.exists(CONFIG_FILE):
        return default_model
    
    try:
        with open(CONFIG_FILE, 'r') as f:
            for line in f:
                if line.startswith('current_model='):
                    return line.split('=')[1].strip()
        return default_model
    except Exception as e:
        print(f"读取配置文件错误: {e}")
        return default_model

# 获取模型路径
def get_model_path():
    model_name = get_current_model_name()
    models_dir = "py/models"  # 修改为与 Spring Boot 后端相同的路径
    
    # 如果是默认模型且models目录下不存在，则使用项目根目录的模型
    if model_name == "best.pt" and not os.path.exists(f"{models_dir}/{model_name}"):
        return "best.pt"
    
    return f"{models_dir}/{model_name}"

# 加载 YOLO 模型
model = YOLO(get_model_path())  # 替换为你的 YOLO 模型路径

@app.route("/process_image", methods=["POST"])
def process_image():
    # 检查是否有文件上传
    if "file" not in request.files:
        return jsonify({"error": "No file uploaded"}), 400

    file = request.files["file"]
    if file.filename == "":
        return jsonify({"error": "No file selected"}), 400

    # 打印当前使用的模型路径
    current_model_path = get_model_path()
    print(f"当前处理图片使用的模型路径: {current_model_path}")

    # 读取文件内容为字节流
    image_bytes = file.read()
    image = cv2.imdecode(np.frombuffer(image_bytes, np.uint8), cv2.IMREAD_COLOR)
    if image is None:
        return jsonify({"error": "Failed to decode image"}), 400

    # 使用 YOLO 模型处理图片
    results = model(image)

    # 解析处理结果并在图片上绘制框
    output_messages = []
    for result in results:
        for box in result.boxes:
            class_id = int(box.cls)
            confidence = float(box.conf)
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            label = model.names[class_id]

            output_messages.append(f"{label} with confidence {confidence:.2f}")

            # 颜色和样式
            box_color = (255, 0, 255)  # 蓝紫色
            text_color = (0, 0, 255)   # 红色
            thickness = 3  # 线条加粗
            font_scale = 0.8  # 字体更大

            # 画矩形框
            cv2.rectangle(image, (x1, y1), (x2, y2), box_color, thickness)

            # 计算文字位置
            text = f"{label} {confidence:.2f}"
            text_size = cv2.getTextSize(text, cv2.FONT_HERSHEY_SIMPLEX, font_scale, thickness)[0]
            text_x, text_y = x1, y1 - 10 if y1 - 10 > 10 else y1 + 30

            # 画背景框
            cv2.rectangle(image, (text_x, text_y - text_size[1] - 5), (text_x + text_size[0], text_y + 5), box_color, -1)
            # 写入文字
            cv2.putText(image, text, (text_x, text_y), cv2.FONT_HERSHEY_SIMPLEX, font_scale, text_color, thickness)

    if not output_messages:
        output_messages.append("无")

    result_messages = ", ".join(output_messages)

    # 生成本地文件名
    timestamp = int(time.time())
    save_filename = f"processed_{timestamp}.jpg"
    save_path = os.path.join(os.getcwd(), save_filename)

    # 保存处理后的图片到本地
    cv2.imwrite(save_path, image)

    # 将处理后的图片编码为 JPG 格式
    _, img_encoded = cv2.imencode(".jpg", image, [int(cv2.IMWRITE_JPEG_QUALITY), 90])
    img_io = io.BytesIO(img_encoded.tobytes())

    # 返回图片和字符串
    response = send_file(img_io, mimetype="image/jpeg", as_attachment=True, download_name="result.jpg")
    response.headers["messages"] = result_messages
    response.headers["saved_file"] = save_filename
    return response

@app.route("/reload_model", methods=["POST"])
def reload_model():
    global model
    try:
        model_path = get_model_path()
        print(f"正在重新加载模型，路径: {model_path}")
        # 重新加载模型
        model = YOLO(model_path)
        print(f"模型加载成功: {model_path}")
        return jsonify({"success": True, "message": f"模型已成功重新加载: {model_path}"})
    except Exception as e:
        print(f"模型加载失败: {str(e)}")
        return jsonify({"success": False, "error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
