import React from "react";
import { useFetch } from "./hooks";

export default function App(): React.ReactElement {
  const { data: msg, loading, error } = useFetch<string>("/api/hello");

  return (
    <div style={{ padding: 20 }}>
      <h1>MiniBuskingBig</h1>
      {loading && <p>Loading...</p>}
      {error && <p style={{ color: "red" }}>Error: {error}</p>}
      {!loading && msg && <p>Backend says: {msg}</p>}
    </div>
  );
}
