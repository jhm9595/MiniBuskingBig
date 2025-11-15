import React from "react";
import { StyleSheet, Text, View, ActivityIndicator } from "react-native";
import { useFetch } from "./hooks";

export default function App(): React.ReactElement {
  const { data: msg, loading, error } = useFetch<string>("/api/hello");

  return (
    <View style={styles.container}>
      <Text style={styles.title}>MiniBuskingBig</Text>
      {loading && <ActivityIndicator size="large" color="#0000ff" />}
      {error && <Text style={styles.error}>Error: {error}</Text>}
      {!loading && msg && <Text style={styles.text}>Backend says: {msg}</Text>}
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
  error: {
    fontSize: 16,
    color: "red",
  },
});
