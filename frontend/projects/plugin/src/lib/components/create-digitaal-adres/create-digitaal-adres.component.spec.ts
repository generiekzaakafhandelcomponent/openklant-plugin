import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateDigitaalAdresComponent} from './create-digitaal-adres.component';
import {EMPTY, of} from "rxjs";
import {CreateDigitaalAdresConfig} from "../../models/create-digitaal-adres-config";

describe('CreateDigitaalAdresComponent', () => {
  let component: CreateDigitaalAdresComponent;
  let fixture: ComponentFixture<CreateDigitaalAdresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateDigitaalAdresComponent]
    })
      .overrideTemplate(CreateDigitaalAdresComponent, "")
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateDigitaalAdresComponent);
    component = fixture.componentInstance;

    component.save$ = EMPTY;
    component.disabled$ = of(false);
    component.pluginId = "test-plugin";

    component.prefillConfiguration$ = of({
      resultPvName: "",
      verstrektDoorBetrokkene: "",
      verstrektDoorPartij: "",
      adres: "",
      soortDigitaalAdres: "",
      isStandaardAdres: true,
      omschrijving: "",
      referentie: "",
      verificatieDatum: "",
    } as CreateDigitaalAdresConfig);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
