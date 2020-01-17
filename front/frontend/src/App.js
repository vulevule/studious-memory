import "bootstrap/dist/css/bootstrap.min.css";
import React from "react";
import { FormCheck, FormControl, FormGroup, FormLabel, FormText, Modal } from "react-bootstrap";
import Button from "react-bootstrap/Button";
import "./App.css";
import RepositoryService from "./services/RepositoryService";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      registerFields: null,
      registerData: null,
      loginFields: null,
      modalShow: false,
      currentTask: null
    };
  }

  formatFields = rawData => {
    let registerFields = {};
    rawData.formFields.map(field => {
      registerFields[field.id] = {
        id: field.id,
        label: field.label,
        type: field.typeName,
        value: null,
        required: field.validationConstraints.length > 0
      };
    });

    return registerFields;
  };

  openRegistration = async () => {
    const registerData = await RepositoryService.startProcess();

    let registerFields = this.formatFields(registerData);

    this.setState({
      registerData,
      registerFields,
      currentTask: "personalData"
    });
    console.log(registerData);
    this.showModal();
  };

  submit = async () => {
    let registerFields = this.state.registerFields;
    let valid = true;

    Object.keys(registerFields).map(key => {
      let field = registerFields[key];
      if (field.required && !field.value) {
        valid = false;
      }
    });

    if (!valid) {
      alert("Please fill all required fields");
      return;
    }

    if (
      registerFields.password &&
      registerFields.repeatPassword &&
      registerFields.password.value !== registerFields.repeatPassword.value
    ) {
      alert("Passwords do not match");
      return;
    }

    console.log(registerFields);

    let sendingData = [];

    Object.keys(registerFields).map(key => {
      let field = registerFields[key];
      sendingData.push({ fieldId: key, fieldValue: field.value });
    });

    let success = await RepositoryService.registerUser(
      this.state.registerData.taskId,
      sendingData
    );

    if (success) {
      if (
        this.state.currentTask === "personalData" ||
        registerFields.enterMore.value
      )
        this.scientificData();
      else {
        alert("Confirmation link is sent to your email");
        this.hideModal();
      }
    }
  };

  scientificData = async () => {
    const registerData = await RepositoryService.scientificData(
      this.state.registerData.processInstanceId
    );
    console.log(registerData);

    let registerFields = this.formatFields(registerData);

    this.setState({
      registerData,
      registerFields,
      currentTask: "scientificData"
    });
  };

  login = () => {
    alert("Under construction");
  };

  showModal = () => {
    this.setState({ modalShow: true });
  };

  hideModal = () => {
    this.setState({ modalShow: false });
  };

  claim = () => {
    RepositoryService.claim(this.state.registerData.taskId);
  }
  
  complete = () => {
    RepositoryService.complete(this.state.registerData.taskId);
  }

  render() {
    let registerFields = this.state.registerFields;

    return (
      <div className="App">
        <header className="App-header">
          {/* <img src={logo} className="App-logo" alt="logo" /> */}
          <div className="btn-group" role="group">
            <Button onClick={this.login}>Log In</Button>
            <Button onClick={this.openRegistration}>Register</Button>
          </div>
        </header>

        <Modal
          show={this.state.modalShow}
          size="lg"
          aria-labelledby="contained-modal-title-vcenter"
          centered
          scrollable
        >
          <Modal.Header>
            <Modal.Title id="contained-modal-title-vcenter">
              Register
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {registerFields &&
              Object.keys(registerFields).map(key => {
                let field = registerFields[key];
                return (
                  <FormGroup key={field.id}>
                    {field.id === "reviewer" || field.type === "boolean" ? (
                      <FormCheck
                        type="checkbox"
                        label={field.label}
                        onChange={e => (field.value = e.target.checked)}
                      ></FormCheck>
                    ) : (
                      <React.Fragment>
                        <FormLabel>{field.label}</FormLabel>
                        <FormControl
                          type={
                            field.id === "password" ||
                            field.id === "repeatPassword"
                              ? "password"
                              : "text"
                          }
                          onChange={e => (field.value = e.target.value)}
                        ></FormControl>
                      </React.Fragment>
                    )}

                    {field.required && <FormText>* Required</FormText>}
                  </FormGroup>
                );
              })}
          </Modal.Body>
          <Modal.Footer>
            <Button
              onClick={this.claim}
            >
              Claim
            </Button>
            <Button
              onClick={this.complete}
            >
              Complete
            </Button>
            <Button onClick={this.submit}>Submit</Button>
          </Modal.Footer>
        </Modal>
      </div>
    );
  }
}

export default App;
