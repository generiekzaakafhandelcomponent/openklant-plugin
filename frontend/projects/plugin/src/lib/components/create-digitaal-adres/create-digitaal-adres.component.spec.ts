import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateDigitaalAdresComponent} from './create-digitaal-adres.component';
import {GetOrCreatePartijComponent} from "../get-or-create-partij/get-or-create-partij.component";
import {EMPTY, of} from "rxjs";
import {GetOrCreatePartijConfig} from "../../models/get-or-create-partij-config";
import {SetDefaultDigitaalAdresConfig} from "../../models/set-default-digitaal-adres-config";

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
      partijUuid: "",
      adres: "",
      soortDigitaalAdres: "",
      verificatieDatum: "",
    } as SetDefaultDigitaalAdresConfig);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
