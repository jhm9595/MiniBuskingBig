import React, { useEffect, useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { apiClient } from "../../shared/api-client";

export default function App(): React.ReactElement {
  const [msg, setMsg] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    apiClient
      .get<string>("/api/hello")
      .then((text) => setMsg(text))
      .catch(() => setMsg("No backend response"))
      .finally(() => setLoading(false));
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>MiniBuskingBig</Text>
      <Text style={styles.text}>
        {loading ? "Loading..." : `Backend says: ${msg}`}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 10,
  },
  text: {
    fontSize: 16,
    color: "#666",
  },
});
