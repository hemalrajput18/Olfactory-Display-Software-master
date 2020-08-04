using System.Linq;
using UnityEngine;
using UnityStandardAssets.Characters.FirstPerson;

public class Loader : MonoBehaviour
{
    public TextAsset config;
    public GameObject northPrefab;
    public GameObject eastPrefab;
    public GameObject roofPrefab;
    public GameObject floorPrefab;
    public FirstPersonController fpsController;

    void Start()
    {
        var splitFile = new string[] { "\r\n", "\r", "\n" };
        var splitDimensions = new string[] { "," };
        var splitRow = new string[] { "+" };

        var lines = config.text.Split(splitFile, System.StringSplitOptions.None);
        var dimensions = lines[0].Split(splitDimensions, System.StringSplitOptions.None);

        fpsController.GetComponent<CharacterController>().enabled = false;
        fpsController.transform.position = new Vector3(4f, 0.1f, 1f);
        fpsController.GetComponent<CharacterController>().enabled = true;
    }
}
