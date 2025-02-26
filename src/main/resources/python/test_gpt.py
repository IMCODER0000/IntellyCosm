from openai import OpenAI
import json
import requests
import time
import sys
import easyocr
import os
from run import ocr_model
import time
import mysql.connector
from mysql.connector import Error
import openai

api_key = os.getenv('OPENAI_API_KEY')
if not api_key:
    raise ValueError("OPENAI_API_KEY environment variable is not set")

openai.api_key = api_key

db_config = {
    'host': 'localhost',
    'database': 'db24106',
    'user': 'dbid241',
    'password': 'dbpass241'
}

def get_query(query, cursor):
    cursor.execute(query)
    tmp = cursor.fetchall()
    return tmp

def get_ingredient():
    connection = mysql.connector.connect(**db_config)
    cursor = connection.cursor()
    query = "select name from ingredient;"
    ingredient = get_query(query, cursor)
    print("show ingredient db")
    cursor.close()
    connection.close()
    return ingredient

def get_cosmetic():
    connection = mysql.connector.connect(**db_config)
    cursor = connection.cursor()
    query = "select name from cosmetic;"
    ingredient = get_query(query, cursor)
    print("show cosmetic db")
    cursor.close()
    connection.close()
    return ingredient

def main():
    start= time.time()


    if len(sys.argv) != 2:
        print("Usage: python gpt_assistant.py <image_path>")
        return

    image_path = sys.argv[1]

    # Specify the image path
    # image_path = '/home/t24106/aidata/image_dataset/Training/Raw/result/cosmetics/images/cosmetics_00020.jpg'



    GPT_MODEL = "gpt-4-turbo"

    thread = openai.Thread.create()
    assistant_id = "asst_dXbPQR4RzCkvkehWzq8jzWzB"

    # OCR 모델 실행
    str_list = ocr_model(image_path)
    content = ' '.join(str_list)
    end = time.time()
    print(f"End OCR MODEL\t{end - start:.5f}sec")
    print(f"============================================\nOCR MODEL RESULT : \n{content}")

    message = openai.Message.create(
        thread_id=thread.id,
        role="user",
        content=content
    )

    run = openai.Run.create(
        thread_id=thread.id,
        assistant_id=assistant_id,
        instructions="OCR 모델에서 추출힌 부정확한 텍스트를 입력받고, 화장품 명은 한개이고,  화장품 성분을 추출하여 정리해서 JSON 형식으로 리턴해줘, 이때 화장품명과 화장품성분은 공백이 있으면 지워줘"
    )

    idx = 0

    while True:
        # print("thread_id: ", thread.id)
        # print("run_id: ", run.id)
        time.sleep(2)

        # 실행 객체를 가져와서 상태를 확인합니다.
        run_status = openai.Run.retrieve(
            thread_id=thread.id,
            run_id=run.id,
        )
        idx = idx+1
        end = time.time()
        print(f"{idx}, {end - start:.5f}sec")

        # print(run_status.model_dump_json(indent=4))

        if run_status.status == "completed":
            # 스레드로부터 메시지를 가져온다.
            messages = openai.Message.list(
                thread_id=thread.id,
            )
            # 가장 최근 답변을 가져온다.
            #print(messages)
            #print(f"{messages.data[0].role.capitalize()}: {messages.data[0].content[0].text.value}")
            # for msg in messages.data:
            #     role = msg.role
            #     content = msg.content[0].text.value
            #     print(f"{role.capitalize()}: {content}")
            # 전체 메시지에서 텍스트 부분만 추출
            text = messages.data[0].content[0].text.value
            print(f"===================================\nGPT ASSISTANT RESULT : \n{text}")

            try:
                json.loads(text)
                print(f"최종결과 : \n{text}")
                break
            except json.JSONDecodeError:
                start_index = text.find("{")
                end_index = text.rfind("}")
                if start_index != -1 and end_index != -1 and end_index > start_index:
                    ingredient_str = text[start_index: end_index+1]  # 중괄호 내용 추출
                    # 중괄호 내용을 JSON으로 파싱
                    try:
                        extracted_data = json.loads(ingredient_str)
                        result = json.dumps(extracted_data,ensure_ascii=False, indent=2)
                        print(f"최종결과 : \n{result}")
                        break
                    except json.JSONDecodeError as e:
                        print("Error: Failed to parse JSON:", e)
                        return None
                else:
                    print("Error: Failed to extract ingredient dictionary from the response.")
                    return None


        elif run_status.status == "requires_action":
            print("requires action...")
            required_actions = (
                run_status.required_action.submit_tool_outputs.model_dump()
            )
            # print("required_actions: ", required_actions)
            tool_outputs = []

            for action in required_actions["tool_calls"]:
                func_name = action["function"]["name"]
                arguments = json.loads(action["function"]["arguments"])
                # print("arguments: ", arguments["location"])

                if func_name == "get_ingredient":
                    output = get_ingredient()
                    tool_outputs.append(
                        {
                            "tool_call_id": action["id"],
                            "output": str(output),
                        }
                    )
                elif func_name == "get_cosmetic":
                    output = get_cosmetic()
                    tool_outputs.append(
                        {
                            "tool_call_id": action["id"],
                            "output": str(output),
                        }
                    )
                else:
                    raise ValueError(f"Unknown function: {func_name}")
            print("Submitting ouputs back to the Assistant...")
            print("run_id: ", run.id)
            # print(tool_outputs)
            openai.Run.submit_tool_outputs(
                thread_id=thread.id,
                run_id=run.id,
                tool_outputs=tool_outputs,
            )
        else:
            print("Not completed yet. Waiting...")
            time.sleep(2)

    end = time.time()
    print(f"{end - start:.5f} sec")

if __name__ == '__main__':
    main()
