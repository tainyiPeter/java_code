#!/usr/bin/env python 
# -*- coding: utf-8 -*- 
# @Time     : 2024/7/31 10:18
# @Author   : Github@AXYZdong
# @File     : websocket_handler.py
# @Software : PyCharm
import ollama
from fastapi import WebSocket

# 显式创建 Ollama 客户端，指定服务地址
ollama_client = ollama.Client(host='http://localhost:11434')


async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    user_input = await websocket.receive_text()

    stream = ollama_client.chat(
        model='llama3.2:latest',
        messages=[{'role': 'user', 'content': user_input}],
        stream=True
    )

    try:
        for chunk in stream:
            model_output = chunk['message']['content']
            await websocket.send_text(model_output)
    except Exception as e:
        await websocket.send_text(f"Error: {e}")
    finally:
        await websocket.close()