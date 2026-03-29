import ollama

print("=== 测试1: 使用默认配置 ===")
try:
    result = ollama.list()
    print("默认配置成功:")
    for model in result['models']:
        print(f"  - {model['name']}")
except Exception as e:
    print(f"默认配置失败: {e}")

print("\n=== 测试2: 显式指定 host ===")
try:
    ollama_client = ollama.Client(host='http://localhost:11434')
    result = ollama_client.list()
    print("显式指定 host 成功:")
    for model in result['models']:
        print(f"  - {model['name']}")
except Exception as e:
    print(f"显式指定 host 失败: {e}")

print("\n=== 测试3: 测试聊天功能 ===")
try:
    ollama_client = ollama.Client(host='http://localhost:11434')
    response = ollama_client.chat(
        model='llama3.2:latest',
        messages=[{'role': 'user', 'content': '你好，请用一句话介绍你自己'}],
        stream=False
    )
    print(f"聊天测试成功: {response['message']['content']}")
except Exception as e:
    print(f"聊天测试失败: {e}")