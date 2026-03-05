import requests
from bs4 import BeautifulSoup
import time
import csv
from fake_useragent import UserAgent


class MaoyanTop100Spider:
    """猫眼电影TOP100榜爬虫"""

    def __init__(self):
        self.base_url = 'https://www.maoyan.com/board/4'
        self.headers = {
            'User-Agent': UserAgent().random,
            'Cookie': '__mta=251635703.1721234567.1234567890.1234567890.123; uuid_n_v=v2; uuid=9C12D4A0C0F111EDB7C3A50812345678'
            # 需要替换为有效Cookie
        }
        self.movies = []

    def fetch_page(self, offset=0):
        """获取单个页面的HTML内容"""
        params = {'offset': offset} if offset else {}
        try:
            response = requests.get(self.base_url, headers=self.headers, params=params, timeout=10)
            response.encoding = 'utf-8'
            if response.status_code == 200:
                return response.text
            else:
                print(f"请求失败，状态码: {response.status_code}")
                return None
        except requests.RequestException as e:
            print(f"请求异常: {e}")
            return None

    def parse_page(self, html):
        """解析页面，提取电影信息"""
        soup = BeautifulSoup(html, 'html.parser')
        # 定位每个电影项
        dd_items = soup.find_all('dd')

        for dd in dd_items:
            # 排名
            rank_tag = dd.find('i', class_='board-index')
            rank = rank_tag.text.strip() if rank_tag else 'N/A'

            # 电影详情页链接
            detail_link_tag = dd.find('a', href=True)
            detail_url = 'https://www.maoyan.com' + detail_link_tag['href'] if detail_link_tag else ''

            # 封面图
            img_tag = dd.find('img', attrs={'data-src': True})
            cover_img = img_tag['data-src'] if img_tag else ''

            # 电影名称
            name_tag = dd.find('p', class_='name')
            name = name_tag.text.strip() if name_tag else ''
            if name_tag and name_tag.a:
                name = name_tag.a.text.strip()

            # 主演
            star_tag = dd.find('p', class_='star')
            star = star_tag.text.strip().replace('主演：', '') if star_tag else ''

            # 上映时间
            release_tag = dd.find('p', class_='releasetime')
            release = release_tag.text.strip().replace('上映时间：', '') if release_tag else ''

            # 评分
            score_tag = dd.find('p', class_='score')
            if score_tag:
                integer = score_tag.find('i', class_='integer').text.strip()
                fraction = score_tag.find('i', class_='fraction').text.strip()
                score = integer + fraction
            else:
                score = 'N/A'

            movie_data = {
                'rank': rank,
                'name': name,
                'score': score,
                'actors': star,
                'release_date': release,
                'cover_url': cover_img,
                'detail_page': detail_url
            }
            self.movies.append(movie_data)
            print(f"已解析: {rank}. {name} - {score}")

    def run(self):
        """主运行逻辑，遍历所有10页"""
        print("开始抓取猫眼电影TOP100...")
        for i in range(10):  # 共10页，offset从0到90
            offset = i * 10
            print(f"正在抓取第 {i + 1} 页，offset={offset}...")
            html = self.fetch_page(offset)
            if html:
                self.parse_page(html)
                time.sleep(2)  # 礼貌等待，避免请求过快
            else:
                print(f"第 {i + 1} 页抓取失败")

        print(f"抓取完成，共获取 {len(self.movies)} 部电影信息")
        self.save_to_csv()

    def save_to_csv(self):
        """保存为CSV文件"""
        filename = 'maoyan_top100.csv'
        with open(filename, 'w', newline='', encoding='utf-8-sig') as f:
            writer = csv.DictWriter(f, fieldnames=['rank', 'name', 'score', 'actors', 'release_date', 'cover_url',
                                                   'detail_page'])
            writer.writeheader()
            writer.writerows(self.movies)
        print(f"数据已保存至 {filename}")


if __name__ == '__main__':
    spider = MaoyanTop100Spider()
    spider.run()