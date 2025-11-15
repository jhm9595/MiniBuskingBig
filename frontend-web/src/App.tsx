import React, { useEffect, useState } from "react";
import { apiClient } from "../../shared/api-client";

export default function App(): React.ReactElement {
  const [msg, setMsg] = useState<string>("");

  useEffect(() => {
    apiClient
      .get<string>("/api/hello")
      .then((text) => setMsg(text))
      .catch(() => setMsg("No backend response"));
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h1>MiniBuskingBig</h1>
      <p>Backend says: {msg}</p>
    </div>
  );
}
