import json
import mysql.connector
from mysql.connector import Error
import sys


def get_arr_to_id(query, arr, cursor):
    res = []
    for id in arr:
        id = int(id)
        cursor.execute(query, (id,))
        tmp = cursor.fetchall()
        tmp_arr = [list(row) for row in tmp]
        res.extend(tmp_arr)
    return res


def calculate_score(id, db_config):
    try:
        # Connect to the database
        connection = mysql.connector.connect(**db_config)

        # Check if connected
        if connection.is_connected():
            print("MySQL 데이터베이스에 성공적으로 연결되었습니다.")
            cursor = connection.cursor()

            # Member ID 가져오기
            query = "select distinct member_id from analysis where analysis_id = %s"
            cursor.execute(query, (id,))
            member_id = cursor.fetchall()[0][0]  # Assuming you only want the first member_id

            # Member skin type 가져오기
            query = "select skin_type from member where member_id = %s"
            cursor.execute(query, (member_id,))
            member_skin_type = cursor.fetchall()[0][0]  # Assuming you only want the first skin_type

            # Ingredient ID 가져오기
            query = "select ingredient_id from analysis_ingredient where analysis_id = %s"
            ingredient_id_arr = get_arr_to_id(query, [id], cursor)

            # positivity status, skin type 가져오기
            query = "select positivity_status, skin_type from skin_type_feature where ingredient_id = %s"
            feature_arr = get_arr_to_id(query, ingredient_id_arr, cursor)
            sensitive_columns = [row[1] for row in feature_arr]
            positive_columns = [row[0] for row in feature_arr]

            # Calculate counts
            positive_cnt = len([value for value in positive_columns if value == 1])
            negative_cnt = len([value for value in positive_columns if value == 0])

            # Calculate score
            tmp_score = 0
            for ingredient in sensitive_columns:
                if ingredient == member_skin_type:
                    tmp_score += 1
            score = tmp_score + positive_cnt - negative_cnt

            # Return score and counts as JSON
            return json.dumps({"score": score, "positive_cnt": positive_cnt, "negative_cnt": negative_cnt})

    except Error as e:
        print("MySQL 연결 중 오류가 발생했습니다:", e)
        return None  # Return a default value in case of error

    finally:
        # Close the database connection
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("MySQL 데이터베이스 연결이 종료되었습니다.")


if __name__ == '__main__':
    # 데이터베이스 연결 정보를 설정합니다.
    db_config = {
        'host': 'localhost',
        'database': 'db24106',
        'user': 'dbid241',
        'password': 'dbpass241'
    }

    # Read ID from standard input
    id = int(sys.stdin.read().strip())

    # Calculate score and print the results
    result = calculate_score(id, db_config)
    if result:
        print(result)
