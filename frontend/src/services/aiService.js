import api from "./api.js";

export async function askAI(type, prompt, context) {
  const { data } = await api.post("/ai/ask", {
    type,
    prompt,
    contextJson: context ? JSON.stringify(context) : undefined,
  });
  return data;
}

export const askChatbot = (prompt, context) => askAI("CHATBOT", prompt, context);
export const getRecommendations = (prompt, context) => askAI("RECOMMENDATION", prompt, context);
export const getStudyPlan = (prompt, context) => askAI("PERSONALIZATION", prompt, context);
