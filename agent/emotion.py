import nltk
from nltk.sentiment import SentimentIntensityAnalyzer


class EmotionalAgent:
    def __init__(self):
        nltk.download('vader_lexicon')
        self.sia = SentimentIntensityAnalyzer()

    def analyze_emotion(self, text):
        sentiment = self.sia.polarity_scores(text)
        if sentiment['compound'] >= 0.05:
            return "Positive"
        elif sentiment['compound'] <= -0.05:
            return "Negative"
        else:
            return "Neutral"

    def respond(self, text):
        emotion = self.analyze_emotion(text)
        if emotion == "Positive":
            return "I'm glad you're feeling positive!"
        elif emotion == "Negative":
            return "I'm sorry you're feeling down. How can I help?"
        else:
            return "I see. Tell me more about how you're feeling."


if __name__ == '__main__':
    # 使用示例
    agent = EmotionalAgent()
    user_input = "I had a sad day today!"
    emotion = agent.analyze_emotion(user_input)
    response = agent.respond(user_input)
    print(f"Detected emotion: {emotion}")
    print(f"Agent response: {response}")