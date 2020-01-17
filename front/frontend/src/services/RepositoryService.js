export default class RepositoryService {
  static async startProcess() {
    return await fetch("/startProcess").then(resp => resp.json());
  }

  static async scientificData(pid) {
    return await fetch(`/scientificData/${pid}`).then(resp => resp.json());
  }

  static async registerUser(taskId, data) {
    let success;

    await fetch(`/post/${taskId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    }).then(
      res => {
        if (res.ok) {
          success = true;
        } else {
          success = false;
          alert(res.text());
        }
      },
      err => {
        success = false;
        alert(err.message);
      }
    );

    return success;
  }

  static async claim(taskId) {
    return await fetch(`/tasks/claim/${taskId}`).then(resp => resp.json());
  }

  static async complete(taskId) {
    return await fetch(`/tasks/complete/${taskId}`).then(resp => resp.json());
  }
}
